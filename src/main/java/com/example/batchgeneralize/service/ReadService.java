package com.example.batchgeneralize.service;

import com.example.batchgeneralize.entity.Country;
import com.example.batchgeneralize.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadService implements IReadService{

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }
}
