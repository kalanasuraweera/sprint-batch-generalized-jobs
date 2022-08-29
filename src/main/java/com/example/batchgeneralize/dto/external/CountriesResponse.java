package com.example.batchgeneralize.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountriesResponse implements ListResponse<CountryItem> {
    @JsonProperty("countries")
    private List<CountryItem> countries;

    @Override
    public List<CountryItem> getList() {
        return countries;
    }
}
