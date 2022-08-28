package com.example.batchgeneralize.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryItem {
    @JsonProperty("countryCode")
    private String countryCode;
    @JsonProperty("countryName")
    private String countryName;
}
