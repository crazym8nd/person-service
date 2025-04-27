package com.bnm.personservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.CountryAuditDTO;
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

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnCountryHistory() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name("Russia")
        .alpha2("RU")
        .alpha3("RUS")
        .status("ACTIVE");

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
        .name("Russia")
        .alpha2("RU")
        .alpha3("RUS")
        .status("INACTIVE");

    mockMvc.perform(put("/api/v1/countries/{id}", countryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем историю изменений
    final MvcResult historyResult = mockMvc.perform(
            get("/api/v1/audit/countries/{id}/history", countryId))
        .andExpect(status().isOk())
        .andReturn();

    final List<CountryAuditDTO> history = objectMapper.readValue(
        historyResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    // Проверяем, что есть две версии
    assertThat(history).hasSize(2);

    // Проверяем первую версию
    final CountryAuditDTO firstVersion = history.get(0);
    assertThat(firstVersion.getName()).isEqualTo("Russia");
    assertThat(firstVersion.getStatus()).isEqualTo("ACTIVE");
    assertThat(firstVersion.getCreatedAt()).isNotNull();
    assertThat(firstVersion.getCreatedBy()).isEqualTo("system");

    // Проверяем вторую версию
    final CountryAuditDTO secondVersion = history.get(1);
    assertThat(secondVersion.getName()).isEqualTo("Russia");
    assertThat(secondVersion.getStatus()).isEqualTo("INACTIVE");
    assertThat(secondVersion.getUpdatedAt()).isNotNull();
    assertThat(secondVersion.getUpdatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturnSpecificRevision() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name("USA")
        .alpha2("US")
        .alpha3("USA")
        .status("ACTIVE");

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
        .name("USA")
        .alpha2("US")
        .alpha3("USA")
        .status("INACTIVE");

    mockMvc.perform(put("/api/v1/countries/{id}", countryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем историю изменений для определения номера ревизии
    final MvcResult historyResult = mockMvc.perform(
            get("/api/v1/audit/countries/{id}/history", countryId))
        .andExpect(status().isOk())
        .andReturn();

    final List<CountryAuditDTO> history = objectMapper.readValue(
        historyResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    // Получаем конкретную ревизию (первую версию)
    final MvcResult revisionResult = mockMvc.perform(
            get("/api/v1/audit/countries/{id}/revisions/1", countryId))
        .andExpect(status().isOk())
        .andReturn();

    final CountryAuditDTO revision = objectMapper.readValue(
        revisionResult.getResponse().getContentAsString(),
        CountryAuditDTO.class
    );

    // Проверяем, что получили правильную версию
    assertThat(revision.getName()).isEqualTo("USA");
    assertThat(revision.getStatus()).isEqualTo("ACTIVE");
    assertThat(revision.getCreatedAt()).isNotNull();
    assertThat(revision.getCreatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturn404ForNonExistentRevision() throws Exception {
    // Создаем страну
    final CountryCreate countryCreate = new CountryCreate()
        .name("France")
        .alpha2("FR")
        .alpha3("FRA")
        .status("ACTIVE");

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
} 