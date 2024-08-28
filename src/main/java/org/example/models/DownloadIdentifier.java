package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DownloadIdentifier {

    @JsonProperty("client")
    private String client;

    @JsonProperty("publisher")
    private int publisher;

    @JsonProperty("podcastId")
    private String podcastId;

    @JsonProperty("showId")
    private String showId;

    @JsonProperty("episodeId")
    private String episodeId;

    @JsonProperty("downloadId")
    private String downloadId;

}
