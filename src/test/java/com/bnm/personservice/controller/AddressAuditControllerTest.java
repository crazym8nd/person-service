package com.bnm.personservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.AddressAuditDTO;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.model.CountryCreate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
class AddressAuditControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private Long countryId;

  @BeforeEach
  void setUp() throws Exception {
    // Создаем страну для тестов
    final CountryCreate countryCreate = new CountryCreate()
        .name("Russia")
        .alpha2("RU")
        .alpha3("RUS")
        .status("ACTIVE");

    final MvcResult createCountryResult = mockMvc.perform(post("/api/v1/countries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(countryCreate)))
        .andExpect(status().isOk())
        .andReturn();

    countryId = objectMapper.readTree(createCountryResult.getResponse().getContentAsString())
        .get("id").asLong();
  }

  @Test
  void shouldReturnAddressHistory() throws Exception {
    // Создаем адрес
    final AddressCreate addressCreate = new AddressCreate()
        .countryId(countryId)
        .address("Lenina st., 1")
        .zipCode("123456")
        .city("Moscow")
        .state("Moscow");

    // Сохраняем адрес
    final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного адреса
    final UUID addressId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Изменяем адрес
    final AddressCreate updateRequest = new AddressCreate()
        .countryId(countryId)
        .address("Lenina st., 2")
        .zipCode("123457")
        .city("Moscow")
        .state("Moscow");

    mockMvc.perform(put("/api/v1/addresses/{id}", addressId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем историю изменений
    final MvcResult historyResult = mockMvc.perform(
            get("/api/v1/audit/addresses/{id}/history", addressId))
        .andExpect(status().isOk())
        .andReturn();

    final List<AddressAuditDTO> history = objectMapper.readValue(
        historyResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    // Проверяем, что есть две версии
    assertThat(history).hasSize(2);

    // Проверяем первую версию
    final AddressAuditDTO firstVersion = history.get(0);
    assertThat(firstVersion.getAddress()).isEqualTo("Lenina st., 1");
    assertThat(firstVersion.getZipCode()).isEqualTo("123456");
    assertThat(firstVersion.getCountryId()).isEqualTo(countryId);
    assertThat(firstVersion.getCountryName()).isEqualTo("Russia");
    assertThat(firstVersion.getCreatedAt()).isNotNull();
    assertThat(firstVersion.getCreatedBy()).isEqualTo("system");

    // Проверяем вторую версию
    final AddressAuditDTO secondVersion = history.get(1);
    assertThat(secondVersion.getAddress()).isEqualTo("Lenina st., 2");
    assertThat(secondVersion.getZipCode()).isEqualTo("123457");
    assertThat(secondVersion.getCountryId()).isEqualTo(countryId);
    assertThat(secondVersion.getCountryName()).isEqualTo("Russia");
    assertThat(secondVersion.getUpdatedAt()).isNotNull();
    assertThat(secondVersion.getUpdatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturnSpecificRevision() throws Exception {
    // Создаем адрес
    final AddressCreate addressCreate = new AddressCreate()
        .countryId(countryId)
        .address("Tverskaya st., 1")
        .zipCode("123456")
        .city("Moscow")
        .state("Moscow");

    // Сохраняем адрес
    final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного адреса
    final UUID addressId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Изменяем адрес
    final AddressCreate updateRequest = new AddressCreate()
        .countryId(countryId)
        .address("Tverskaya st., 2")
        .zipCode("123457")
        .city("Moscow")
        .state("Moscow");

    mockMvc.perform(put("/api/v1/addresses/{id}", addressId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем конкретную ревизию (первую версию)
    final MvcResult revisionResult = mockMvc.perform(
            get("/api/v1/audit/addresses/{id}/revision/2", addressId))
        .andExpect(status().isOk())
        .andReturn();

    final AddressAuditDTO revision = objectMapper.readValue(
        revisionResult.getResponse().getContentAsString(),
        AddressAuditDTO.class
    );

    // Проверяем, что получили правильную версию
    assertThat(revision.getAddress()).isEqualTo("Tverskaya st., 1");
    assertThat(revision.getZipCode()).isEqualTo("123456");
    assertThat(revision.getCountryId()).isEqualTo(countryId);
    assertThat(revision.getCountryName()).isEqualTo("Russia");
    assertThat(revision.getCreatedAt()).isNotNull();
    assertThat(revision.getCreatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturn404ForNonExistentRevision() throws Exception {
    // Создаем адрес
    final AddressCreate addressCreate = new AddressCreate()
        .countryId(countryId)
        .address("Nevsky pr., 1")
        .zipCode("198000")
        .city("Saint Petersburg")
        .state("Saint Petersburg");

    // Сохраняем адрес
    final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного адреса
    final UUID addressId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Пытаемся получить несуществующую ревизию
    mockMvc.perform(get("/api/v1/audit/addresses/{id}/revision/999", addressId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404ForNonExistentAddress() throws Exception {
    final UUID nonExistentId = UUID.randomUUID();

    // Пытаемся получить историю несуществующего адреса
    mockMvc.perform(get("/api/v1/audit/addresses/{id}/history", nonExistentId))
        .andExpect(status().isNotFound());

    // Пытаемся получить ревизию несуществующего адреса
    mockMvc.perform(get("/api/v1/audit/addresses/{id}/revision/1", nonExistentId))
        .andExpect(status().isNotFound());
  }
} 