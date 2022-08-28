package com.example.batchgeneralize.controller;

import com.example.batchgeneralize.dto.external.CountriesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExternalApiMockController implements IExternalApiMockController {

    @Override
    public CountriesResponse getCountries() {
        Resource resource = new ClassPathResource("responses/Countries.json");
        ObjectMapper mapper = new ObjectMapper();
        CountriesResponse response;
        try {
            response = mapper.readValue(resource.getInputStream(), CountriesResponse.class);
        } catch (Exception e) {
            return new CountriesResponse();
        }
        return response;
    }
}
