package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Zone1 {

    @JsonProperty("id")
    private int id;

    @JsonProperty("maxAds")
    private int maxAds;

    @JsonProperty("maxDuration")
    private int maxDuration;

}
