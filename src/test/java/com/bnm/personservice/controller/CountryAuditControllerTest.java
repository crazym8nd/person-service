package com.bnm.personservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.CountryAudit;
import com.bnm.personservice.model.CountryCreate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CountryAuditControllerTest {

  private static final String STATUS_ACTIVE = "ACTIVE";
  private static final String STATUS_INACTIVE = "INACTIVE";
  private static final String RUSSIA_NAME = "Russia";
  private static final String RUSSIA_ALPHA2 = "RU";
  private static final String RUSSIA_ALPHA3 = "RUS";
  private static final String USA_NAME = "USA";
  private static final String USA_ALPHA2 = "US";
  private static final String USA_ALPHA3 = "USA";
  private static final String FRANCE_NAME = "France";
  private static final String FRANCE_ALPHA2 = "FR";
  private static final String FRANCE_ALPHA3 = "FRA";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnCountryHistory() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name(RUSSIA_NAME)
        .alpha2(RUSSIA_ALPHA2)
        .alpha3(RUSSIA_ALPHA3)
        .status(STATUS_ACTIVE);

    // Сохраняем страну
    final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(countryCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданной страны
    final Long countryId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asLong();

    // Изменяем статус страны
    final CountryCreate updateRequest = new CountryCreate()
        .name(RUSSIA_NAME)
        .alpha2(RUSSIA_ALPHA2)
        .alpha3(RUSSIA_ALPHA3)
        .status(STATUS_INACTIVE);

    mockMvc.perform(put("/api/v1/countries/{id}", countryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем историю изменений
    final MvcResult historyResult = mockMvc.perform(
            get("/api/v1/audit/countries/{id}/history", countryId))
        .andExpect(status().isOk())
        .andReturn();

    final List<CountryAudit> history = objectMapper.readValue(
        historyResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    // Проверяем, что есть две версии
    assertThat(history).hasSize(2);

    // Проверяем первую версию
    final CountryAudit firstVersion = history.get(0);
    assertThat(firstVersion.getName()).isEqualTo(RUSSIA_NAME);
    assertThat(firstVersion.getStatus()).isEqualTo(STATUS_ACTIVE);
    assertThat(firstVersion.getRevisionInstant()).isNotNull();
    assertThat(firstVersion.getRevisionType()).isEqualTo(CountryAudit.RevisionTypeEnum.ADD);

    // Проверяем вторую версию
    final CountryAudit secondVersion = history.get(1);
    assertThat(secondVersion.getName()).isEqualTo(RUSSIA_NAME);
    assertThat(secondVersion.getStatus()).isEqualTo(STATUS_INACTIVE);
    assertThat(secondVersion.getRevisionInstant()).isNotNull();
    assertThat(secondVersion.getRevisionType()).isEqualTo(CountryAudit.RevisionTypeEnum.MOD);
  }

  @Test
  void shouldReturnSpecificRevision() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name(USA_NAME)
        .alpha2(USA_ALPHA2)
        .alpha3(USA_ALPHA3)
        .status(STATUS_ACTIVE);

    // Сохраняем страну
    final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(countryCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданной страны
    final Long countryId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asLong();

    // Изменяем статус страны
    final CountryCreate updateRequest = new CountryCreate()
        .name(USA_NAME)
        .alpha2(USA_ALPHA2)
        .alpha3(USA_ALPHA3)
        .status(STATUS_INACTIVE);

    mockMvc.perform(put("/api/v1/countries/{id}", countryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем конкретную ревизию (первую версию)
    final MvcResult revisionResult = mockMvc.perform(
            get("/api/v1/audit/countries/{id}/revisions/1", countryId))
        .andExpect(status().isOk())
        .andReturn();

    final CountryAudit revision = objectMapper.readValue(
        revisionResult.getResponse().getContentAsString(),
        CountryAudit.class
    );

    // Проверяем, что получили правильную версию
    assertThat(revision.getName()).isEqualTo(USA_NAME);
    assertThat(revision.getStatus()).isEqualTo(STATUS_ACTIVE);
    assertThat(revision.getRevisionInstant()).isNotNull();
    assertThat(revision.getRevisionType()).isEqualTo(CountryAudit.RevisionTypeEnum.ADD);
  }

  @Test
  void shouldReturn404ForNonExistentRevision() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name(FRANCE_NAME)
        .alpha2(FRANCE_ALPHA2)
        .alpha3(FRANCE_ALPHA3)
        .status(STATUS_ACTIVE);

    // Сохраняем страну
    final MvcResult createResult = mockMvc.perform(post("/api/v1/countries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(countryCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданной страны
    final Long countryId = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asLong();

    // Пытаемся получить несуществующую ревизию
    mockMvc.perform(get("/api/v1/audit/countries/{id}/revisions/999", countryId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404ForNonExistentCountry() throws Exception {
    // Пытаемся получить историю несуществующей страны
    mockMvc.perform(get("/api/v1/audit/countries/{id}/history", 999L))
        .andExpect(status().isNotFound());

    // Пытаемся получить ревизию несуществующей страны
    mockMvc.perform(get("/api/v1/audit/countries/{id}/revisions/1", 999L))
        .andExpect(status().isNotFound());
  }
} 