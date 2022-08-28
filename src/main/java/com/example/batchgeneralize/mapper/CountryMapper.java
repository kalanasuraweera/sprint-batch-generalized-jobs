package com.example.batchgeneralize.mapper;

import com.example.batchgeneralize.dto.external.CountriesResponse;
import com.example.batchgeneralize.dto.external.CountryItem;
import com.example.batchgeneralize.entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CountryMapper {

    public static final CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    public CountriesResponse mapCountriesListToCountriesResponse(List<Country> countryList) {
        CountriesResponse response = new CountriesResponse();
        List<CountryItem> countryItemList = new ArrayList<>();
        response.setCountries(countryItemList);
        if (countryList != null && !countryList.isEmpty()) {
            countryList.forEach(country -> countryItemList.add(INSTANCE.mapCountryToCountryItem(country)));
        }
        return response;
    }

    @Mapping(source = "code", target = "countryCode")
    @Mapping(source = "name", target = "countryName")
    public abstract CountryItem mapCountryToCountryItem(Country country);
}
