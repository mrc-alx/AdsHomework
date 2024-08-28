package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Opportunity;
import org.example.responses.DownloadResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AdsHomeworkTests {

    private static final String downloadsFilePath = "src/test/resources/downloads.txt";
    private static final List<DownloadResponse> downloadResponses = new ArrayList<>();
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final long weeklyOffset = 604_800_000L;
    private static final String expectedMostListenedPodcastInCity = "san francisco";
    private static final String prerollKeyword = "preroll";

    private static final Map.Entry<String, Long> expectedMostListenedPodcast = entry("Who Trolled Amber", 24L);
    private static final Map.Entry<String, Long> expectedMostUsedDevice = entry("mobiles & tablets", 60L);
    private static final Map<String, Long> expectedPrerollOpportunities = Map.ofEntries(
            entry("Stuff You Should Know", 40L),
            entry("Who Trolled Amber", 40L),
            entry("Crime Junkie", 30L),
            entry("The Joe Rogan Experience", 10L)
    );
    private static final Map<String, OffsetDateTime> expectedWeeklyPodcasts = Map.ofEntries(
            entry("Crime Junkie", getWeeklyOffsetBasedOnTimestamp(LocalDateTime.of(
                    2024,
                    5,
                    15,
                    22,
                    0
            ).toInstant(zoneOffset).toEpochMilli())),
            entry("Who Trolled Amber", getWeeklyOffsetBasedOnTimestamp(LocalDateTime.of(
                    2024,
                    5,
                    13,
                    20,
                    0
            ).toInstant(zoneOffset).toEpochMilli()))
    );

    @BeforeAll
    public static void setup() {
        File downloadsFile = new File(downloadsFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        try (Scanner fileReader = new Scanner(downloadsFile)) {
            while (fileReader.hasNextLine()) {
                DownloadResponse downloadResponse = objectMapper.readValue(fileReader.nextLine(), DownloadResponse.class);
                downloadResponses.add(downloadResponse);
            }
        } catch (FileNotFoundException | JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void t3_mostListenedPodcast() {
        Optional<Map.Entry<String, Long>> filteredResult = downloadResponses.stream()
                .filter(entry -> entry.getCity().equals(expectedMostListenedPodcastInCity))
                .collect(Collectors.groupingBy(entry -> entry.getDownloadIdentifier().getShowId(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue());

        if (filteredResult.isEmpty()) {
            fail("There aren't any results based on the given filter criteria");
        }

        printMostPopular("show", filteredResult.get());

        assertEquals(filteredResult.get(), expectedMostListenedPodcast);
    }

    @Test
    public void t4_mostUsedDevices() {
        Optional<Map.Entry<String, Long>> filteredResult = downloadResponses.stream()
                .collect(Collectors.groupingBy(DownloadResponse::getDeviceType, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue());

        if (filteredResult.isEmpty()) {
            fail("There aren't any results based on the given filter criteria");
        }

        printMostPopular("device", filteredResult.get());

        assertEquals(filteredResult.get(), expectedMostUsedDevice);
    }

    @Test
    public void t5_prerollOpportunities() {
        Map<String, Long> filteredResults = downloadResponses.stream()
                .collect(Collectors.toMap(
                        key -> key.getDownloadIdentifier().getShowId(),
                        value -> value.getOpportunities().stream().filter(
                                opportunity -> opportunity.getPositionUrlSegments().getAdBreakIndex().contains(prerollKeyword)).count(), Long::sum));

        printPrerollOpportunities(filteredResults);

        assertEquals(expectedPrerollOpportunities, filteredResults);
    }

    @Test
    public void t6_weeklyPodcasts() {
        Map<String, OffsetDateTime> filteredResults = downloadResponses.stream()
                .collect(Collectors.toMap(
                        key -> key.getDownloadIdentifier().getShowId(),
                        value -> value.getOpportunities().stream()
                                .map(Opportunity::getOriginalEventTime)
                                .collect(Collectors.toList()),
                        (existingList, newList) -> Stream.concat(existingList.stream(), newList.stream()).distinct().toList()))
                .entrySet().stream()
                .filter(podcast -> areWeekly(podcast.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        value -> getWeeklyOffsetBasedOnTimestamp(value.getValue().getFirst())));

        filteredResults.values().forEach(this::printDate);

        assertEquals(filteredResults, expectedWeeklyPodcasts);
    }

    private void printMostPopular(String type, Map.Entry<String, Long> entry) {
        System.out.println("Most popular " + type + " is: " + entry.getKey());
        System.out.println("Number of downloads is: " + entry.getValue());
        System.out.println();
    }

    private static void printPrerollOpportunities(Map<String, Long> results) {
        results.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry ->
                        System.out.println(
                                "Show Id: " + entry.getKey() + ", Preroll Opportunity Number: " + entry.getValue()));
        System.out.println();
    }

    private static boolean areWeekly(List<Long> timestamps) {
        if (timestamps.isEmpty() || timestamps.size() == 1) {
            return true;
        }

        OffsetDateTime referenceDateTime = getWeeklyOffsetBasedOnTimestamp(timestamps.getFirst());

        for (long timestamp : timestamps) {
            OffsetDateTime currentDateTime = getWeeklyOffsetBasedOnTimestamp(timestamp);

            if (currentDateTime.getDayOfWeek().getValue() != referenceDateTime.getDayOfWeek().getValue() ||
                    currentDateTime.getHour() != referenceDateTime.getHour() ||
                    currentDateTime.getMinute() != referenceDateTime.getMinute() ||
                    currentDateTime.getSecond() != referenceDateTime.getSecond()) {
                return false;
            }
        }

        return true;
    }

    private static OffsetDateTime getWeeklyOffsetBasedOnTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp % weeklyOffset).atOffset(zoneOffset);
    }

    private void printDate(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E HH:mm");
        System.out.println(dateTime.format(formatter));
    }

}
