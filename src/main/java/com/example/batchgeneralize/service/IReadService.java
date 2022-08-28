package com.example.batchgeneralize.service;

import com.example.batchgeneralize.entity.Country;

import java.util.List;

public interface IReadService {
    List<Country> getCountries();
}
