package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Zones {

    @JsonProperty("zone1")
    private Zone1 zone1;

}
