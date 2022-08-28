package com.example.batchgeneralize.controller;

import com.example.batchgeneralize.dto.external.CountriesResponse;
import com.example.batchgeneralize.mapper.CountryMapper;
import com.example.batchgeneralize.service.IReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadController implements IReadController {
    @Autowired
    private IReadService readService;

    @Override
    public CountriesResponse getCountries() {
        return CountryMapper.INSTANCE.mapCountriesListToCountriesResponse(readService.getCountries());
    }
}
