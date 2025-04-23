package com.bnm.personservice.controller;

import com.bnm.personservice.api.DefaultApi;
import com.bnm.personservice.mapper.CountryMapper;
import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import com.bnm.personservice.service.CountryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CountryController implements DefaultApi {
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Override
    public ResponseEntity<List<Country>> getCountries() {
        return ResponseEntity.ok(
                countryService.getAllCountries().stream()
                        .map(countryMapper::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<Country> getCountryById(Integer id) {
        return ResponseEntity.ok(
                countryMapper.toDto(
                        countryService.getCountryById(id.longValue())
                )
        );
    }

    @Override
    public ResponseEntity<Country> createCountry(CountryCreate countryCreate) {
        return ResponseEntity.ok(
                countryMapper.toDto(
                        countryService.createCountry(
                                countryMapper.toEntity(countryCreate)
                        )
                )
        );
    }
} 