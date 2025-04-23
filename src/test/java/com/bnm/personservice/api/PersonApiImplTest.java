package com.bnm.personservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class PersonApiImplTest {

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

    // Проверка полей аудита
    assertThat(createdCountry.getCreated()).isNotNull()
        .isBefore(OffsetDateTime.now());
    assertThat(createdCountry.getUpdated()).isNotNull()
        .isBefore(OffsetDateTime.now());
  }
} 