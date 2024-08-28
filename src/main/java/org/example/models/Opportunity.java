package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Opportunity {

    @JsonProperty("originalEventTime")
    private long originalEventTime;

    @JsonProperty("maxDuration")
    private int maxDuration;

    @JsonProperty("zones")
    private Zones zones;

    @JsonProperty("positionUrlSegments")
    private PositionUrlSegments positionUrlSegments;

    @JsonProperty("insertionRate")
    private int insertionRate;

}
