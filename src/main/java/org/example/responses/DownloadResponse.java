package org.example.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.models.DownloadIdentifier;
import org.example.models.Opportunity;

import java.util.List;

@Data
public class DownloadResponse {


    @JsonProperty("downloadIdentifier")
    private DownloadIdentifier downloadIdentifier;

    @JsonProperty("opportunities")
    private List<Opportunity> opportunities;

    @JsonProperty("agency")
    private int agency;

    @JsonProperty("deviceType")
    private String deviceType;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("listenerId")
    private String listenerId;

}
