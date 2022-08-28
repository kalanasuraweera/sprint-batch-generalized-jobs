package com.example.batchgeneralize.controller;

import com.example.batchgeneralize.dto.external.CountriesResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/external")
public interface IExternalApiMockController {
    @GetMapping("/countries")
    CountriesResponse getCountries();
}
