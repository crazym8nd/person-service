package com.bnm.personservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.model.CountryCreate;
import com.bnm.personservice.model.IndividualAuditDTO;
import com.bnm.personservice.model.IndividualCreate;
import com.bnm.personservice.model.UserCreate;
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
class IndividualAuditControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID userId;

  @BeforeEach
  void setUp() throws Exception {
    // Создаем страну
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

    final Long countryId = objectMapper.readTree(
            createCountryResult.getResponse().getContentAsString())
        .get("id").asLong();

    // Создаем адрес
    final AddressCreate addressCreate = new AddressCreate()
        .countryId(countryId)
        .address("Lenina st., 1")
        .zipCode("123456")
        .city("Moscow")
        .state("Moscow");

    final MvcResult createAddressResult = mockMvc.perform(post("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressCreate)))
        .andExpect(status().isOk())
        .andReturn();

    final UUID addressId = UUID.fromString(objectMapper.readTree(
            createAddressResult.getResponse().getContentAsString())
        .get("id").asText());

    // Создаем пользователя
    final UserCreate userCreate = new UserCreate()
        .firstName("Ivan")
        .lastName("Ivanov")
        .addressId(addressId)
        .status("ACTIVE");

    final MvcResult createUserResult = mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userCreate)))
        .andExpect(status().isOk())
        .andReturn();

    userId = UUID.fromString(objectMapper.readTree(
            createUserResult.getResponse().getContentAsString())
        .get("id").asText());
  }

  @Test
  void shouldReturnIndividualHistory() throws Exception {
    // Создаем физическое лицо
    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(userId)
        .passportNumber("1234 567890")
        .phoneNumber("+7 (999) 123-45-67")
        .email("ivan@example.com");

    // Сохраняем физическое лицо
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного физического лица
    final UUID individualId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Изменяем физическое лицо
    final IndividualCreate updateRequest = new IndividualCreate()
        .userId(userId)
        .passportNumber("1234 567890")
        .phoneNumber("+7 (999) 999-99-99")
        .email("ivan.ivanov@example.com");

    mockMvc.perform(put("/api/v1/individuals/{id}", individualId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем историю изменений
    final MvcResult historyResult = mockMvc.perform(
            get("/api/v1/audit/individuals/{id}/history", individualId))
        .andExpect(status().isOk())
        .andReturn();

    final List<IndividualAuditDTO> history = objectMapper.readValue(
        historyResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    // Проверяем, что есть две версии
    assertThat(history).hasSize(2);

    // Проверяем первую версию
    final IndividualAuditDTO firstVersion = history.get(0);
    assertThat(firstVersion.getPassportNumber()).isEqualTo("1234 567890");
    assertThat(firstVersion.getPhoneNumber()).isEqualTo("+7 (999) 123-45-67");
    assertThat(firstVersion.getEmail()).isEqualTo("ivan@example.com");
    assertThat(firstVersion.getUserId()).isEqualTo(userId);
    assertThat(firstVersion.getFirstName()).isEqualTo("Ivan");
    assertThat(firstVersion.getLastName()).isEqualTo("Ivanov");
    assertThat(firstVersion.getStatus()).isEqualTo("ACTIVE");
    assertThat(firstVersion.getCreatedAt()).isNotNull();
    assertThat(firstVersion.getCreatedBy()).isEqualTo("system");

    // Проверяем вторую версию
    final IndividualAuditDTO secondVersion = history.get(1);
    assertThat(secondVersion.getPassportNumber()).isEqualTo("1234 567890");
    assertThat(secondVersion.getPhoneNumber()).isEqualTo("+7 (999) 999-99-99");
    assertThat(secondVersion.getEmail()).isEqualTo("ivan.ivanov@example.com");
    assertThat(secondVersion.getUserId()).isEqualTo(userId);
    assertThat(secondVersion.getFirstName()).isEqualTo("Ivan");
    assertThat(secondVersion.getLastName()).isEqualTo("Ivanov");
    assertThat(secondVersion.getStatus()).isEqualTo("ACTIVE");
    assertThat(secondVersion.getUpdatedAt()).isNotNull();
    assertThat(secondVersion.getUpdatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturnSpecificRevision() throws Exception {
    // Создаем физическое лицо
    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(userId)
        .passportNumber("9876 543210")
        .phoneNumber("+7 (999) 123-45-67")
        .email("petr@example.com");

    // Сохраняем физическое лицо
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного физического лица
    final UUID individualId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Изменяем физическое лицо
    final IndividualCreate updateRequest = new IndividualCreate()
        .userId(userId)
        .passportNumber("9876 543210")
        .phoneNumber("+7 (999) 999-99-99")
        .email("petr.petrov@example.com");

    mockMvc.perform(put("/api/v1/individuals/{id}", individualId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk());

    // Получаем конкретную ревизию (первую версию)
    final MvcResult revisionResult = mockMvc.perform(
            get("/api/v1/audit/individuals/{id}/revision/4", individualId))
        .andExpect(status().isOk())
        .andReturn();

    final IndividualAuditDTO revision = objectMapper.readValue(
        revisionResult.getResponse().getContentAsString(),
        IndividualAuditDTO.class
    );

    // Проверяем, что получили правильную версию
    assertThat(revision.getPassportNumber()).isEqualTo("9876 543210");
    assertThat(revision.getPhoneNumber()).isEqualTo("+7 (999) 123-45-67");
    assertThat(revision.getEmail()).isEqualTo("petr@example.com");
    assertThat(revision.getUserId()).isEqualTo(userId);
    assertThat(revision.getFirstName()).isEqualTo("Ivan");
    assertThat(revision.getLastName()).isEqualTo("Ivanov");
    assertThat(revision.getStatus()).isEqualTo("ACTIVE");
    assertThat(revision.getCreatedAt()).isNotNull();
    assertThat(revision.getCreatedBy()).isEqualTo("system");
  }

  @Test
  void shouldReturn404ForNonExistentRevision() throws Exception {
    // Создаем физическое лицо
    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(userId)
        .passportNumber("5555 555555")
        .phoneNumber("+7 (999) 123-45-67")
        .email("alex@example.com");

    // Сохраняем физическое лицо
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // Получаем ID созданного физического лица
    final UUID individualId = UUID.fromString(objectMapper.readTree(
            createResult.getResponse().getContentAsString())
        .get("id").asText());

    // Пытаемся получить несуществующую ревизию
    mockMvc.perform(get("/api/v1/audit/individuals/{id}/revision/999", individualId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404ForNonExistentIndividual() throws Exception {
    final UUID nonExistentId = UUID.randomUUID();

    // Пытаемся получить историю несуществующего физического лица
    mockMvc.perform(get("/api/v1/audit/individuals/{id}/history", nonExistentId))
        .andExpect(status().isNotFound());

    // Пытаемся получить ревизию несуществующего физического лица
    mockMvc.perform(get("/api/v1/audit/individuals/{id}/revision/1", nonExistentId))
        .andExpect(status().isNotFound());
  }
} 