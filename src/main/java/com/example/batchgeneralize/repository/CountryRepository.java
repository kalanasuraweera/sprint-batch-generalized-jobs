package com.example.batchgeneralize.repository;

import com.example.batchgeneralize.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "CountryRepository")
public interface CountryRepository extends JpaRepository<Country, String> {
}
