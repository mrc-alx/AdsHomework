package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PositionUrlSegments {

    @JsonProperty("aw_0_ais.adBreakIndex")
    private List<String> adBreakIndex;

    @JsonProperty("aw_0_ais.nextEventMs")
    private List<String> nextEventMs;

}
