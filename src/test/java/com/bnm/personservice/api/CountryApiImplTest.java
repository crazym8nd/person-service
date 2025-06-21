package com.bnm.personservice.api;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CountryApiImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCountry_shouldCreateNewCountry() throws Exception {
        // given
        final CountryCreate countryCreate = new CountryCreate()
                .name("Russia")
                .alpha2("RU")
                .alpha3("RUS")
                .status("ACTIVE");

        // when
        final MvcResult mvcResult = mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryCreate)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        final Country createdCountry = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Country.class
        );

        assertThat(createdCountry.getId()).isNotNull();
        assertThat(createdCountry.getName()).isEqualTo(countryCreate.getName());
        assertThat(createdCountry.getAlpha2()).isEqualTo(countryCreate.getAlpha2());
        assertThat(createdCountry.getAlpha3()).isEqualTo(countryCreate.getAlpha3());
        assertThat(createdCountry.getStatus()).isEqualTo(countryCreate.getStatus());
        assertThat(createdCountry.getCreated()).isNotNull()
                .isBefore(OffsetDateTime.now());
        assertThat(createdCountry.getUpdated()).isNotNull()
                .isBefore(OffsetDateTime.now());
    }

    @Test
    void getCountries_shouldReturnListOfCountries() throws Exception {
        // given
        final CountryCreate countryCreate = new CountryCreate()
                .name("USA")
                .alpha2("US")
                .alpha3("USA")
                .status("ACTIVE");

        // Создаем страну для теста
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryCreate)))
                .andExpect(status().isOk());

        // when
        final MvcResult mvcResult = mockMvc.perform(get("/api/v1/countries"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        final List<Country> countries = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(countries).hasSize(1);
        final Country country = countries.get(0);
        assertThat(country.getId()).isNotNull();
        assertThat(country.getName()).isEqualTo(countryCreate.getName());
        assertThat(country.getAlpha2()).isEqualTo(countryCreate.getAlpha2());
        assertThat(country.getAlpha3()).isEqualTo(countryCreate.getAlpha3());
        assertThat(country.getStatus()).isEqualTo(countryCreate.getStatus());
    }

    @Test
    void getCountryById_shouldReturnCountry() throws Exception {
        // given
        final CountryCreate countryCreate = new CountryCreate()
                .name("Germany")
                .alpha2("DE")
                .alpha3("DEU")
                .status("ACTIVE");

        // Создаем страну для теста
        final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryCreate)))
                .andExpect(status().isOk())
                .andReturn();

        final Country createdCountry = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                Country.class
        );

        // when
        final MvcResult mvcResult = mockMvc.perform(
                        get("/api/v1/countries/{id}", createdCountry.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        final Country country = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Country.class
        );

        assertThat(country.getId()).isEqualTo(createdCountry.getId());
        assertThat(country.getName()).isEqualTo(countryCreate.getName());
        assertThat(country.getAlpha2()).isEqualTo(countryCreate.getAlpha2());
        assertThat(country.getAlpha3()).isEqualTo(countryCreate.getAlpha3());
        assertThat(country.getStatus()).isEqualTo(countryCreate.getStatus());
    }

    @Test
    void updateCountry_shouldUpdateExistingCountry() throws Exception {
        // given
        final CountryCreate countryCreate = new CountryCreate()
                .name("France")
                .alpha2("FR")
                .alpha3("FRA")
                .status("ACTIVE");

        // Создаем страну для теста
        final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryCreate)))
                .andExpect(status().isOk())
                .andReturn();

        final Country createdCountry = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                Country.class
        );

        final CountryCreate updateRequest = new CountryCreate()
                .name("France Updated")
                .alpha2("FR")
                .alpha3("FRA")
                .status("INACTIVE");

        // when
        final MvcResult mvcResult = mockMvc.perform(
                        put("/api/v1/countries/{id}", createdCountry.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        final Country updatedCountry = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Country.class
        );

        assertThat(updatedCountry.getId()).isEqualTo(createdCountry.getId());
        assertThat(updatedCountry.getName()).isEqualTo(updateRequest.getName());
        assertThat(updatedCountry.getAlpha2()).isEqualTo(updateRequest.getAlpha2());
        assertThat(updatedCountry.getAlpha3()).isEqualTo(updateRequest.getAlpha3());
        assertThat(updatedCountry.getStatus()).isEqualTo(updateRequest.getStatus());
        assertThat(updatedCountry.getUpdated()).isAfter(createdCountry.getUpdated());
    }

    @Test
    void deleteCountry_shouldDeleteExistingCountry() throws Exception {
        // given
        final CountryCreate countryCreate = new CountryCreate()
                .name("Italy")
                .alpha2("IT")
                .alpha3("ITA")
                .status("ACTIVE");

        // Создаем страну для теста
        final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(countryCreate)))
                .andExpect(status().isOk())
                .andReturn();

        final Country createdCountry = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                Country.class
        );

        // when
        mockMvc.perform(delete("/api/v1/countries/{id}", createdCountry.getId()))
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(get("/api/v1/countries/{id}", createdCountry.getId()))
                .andExpect(status().isNotFound());
    }
} 