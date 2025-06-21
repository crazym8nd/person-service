package com.bnm.personservice.controller;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.model.CountryCreate;
import com.bnm.personservice.model.UserAudit;
import com.bnm.personservice.model.UserAudit.RevisionTypeEnum;
import com.bnm.personservice.model.UserCreate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserAuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID addressId;

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

        addressId = UUID.fromString(objectMapper.readTree(
                        createAddressResult.getResponse().getContentAsString())
                .get("id").asText());
    }

    @Test
    void shouldReturnUserHistory() throws Exception {
        // Создаем пользователя
        final UserCreate userCreate = new UserCreate()
                .firstName("Ivan")
                .lastName("Ivanov")
                .addressId(addressId)
                .status("ACTIVE");

        // Сохраняем пользователя
        final MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isOk())
                .andReturn();

        // Получаем ID созданного пользователя
        final UUID userId = UUID.fromString(objectMapper.readTree(
                        createResult.getResponse().getContentAsString())
                .get("id").asText());

        // Изменяем пользователя
        final UserCreate updateRequest = new UserCreate()
                .firstName("Ivan")
                .lastName("Petrov")
                .addressId(addressId)
                .status("INACTIVE");

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Получаем список всех ревизий
        final MvcResult historyResult = mockMvc.perform(
                        get("/api/v1/audit/users/{id}/history", userId))
                .andExpect(status().isOk())
                .andReturn();

        final List<UserAudit> history = objectMapper.readValue(
                historyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(history).isNotEmpty();
        System.out.println("История ревизий:");
        history.forEach(audit -> System.out.println("Ревизия #" + audit.getRevisionNumber() +
                ", тип: " + audit.getRevisionType() +
                ", имя: " + audit.getFirstName() +
                ", фамилия: " + audit.getLastName() +
                ", статус: " + audit.getStatus()));

        final Integer firstRevisionNumber = history.get(0).getRevisionNumber();

        // Получаем конкретную ревизию (первую версию)
        final MvcResult revisionResult = mockMvc.perform(
                        get("/api/v1/audit/users/{id}/revision/{rev}", userId, firstRevisionNumber))
                .andExpect(status().isOk())
                .andReturn();

        final UserAudit revision = objectMapper.readValue(
                revisionResult.getResponse().getContentAsString(),
                UserAudit.class
        );

        // Проверяем, что получили правильную версию
        assertThat(revision.getFirstName()).isEqualTo("Ivan");
        assertThat(revision.getLastName()).isEqualTo("Ivanov");
        assertThat(revision.getStatus()).isEqualTo("ACTIVE");
        assertThat(revision.getAddressId()).isEqualTo(addressId);
        assertThat(revision.getRevisionInstant()).isNotNull();
        assertThat(revision.getRevisionType()).isEqualTo(UserAudit.RevisionTypeEnum.ADD);
    }

    @Test
    void shouldReturnSpecificRevision() throws Exception {
        // Создаем пользователя
        final UserCreate userCreate = new UserCreate()
                .firstName("Petr")
                .lastName("Petrov")
                .addressId(addressId)
                .status("ACTIVE");

        // Сохраняем пользователя
        final MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isOk())
                .andReturn();

        // Получаем ID созданного пользователя
        final UUID userId = UUID.fromString(objectMapper.readTree(
                        createResult.getResponse().getContentAsString())
                .get("id").asText());

        // Изменяем пользователя
        final UserCreate updateRequest = new UserCreate()
                .firstName("Petr")
                .lastName("Sidorov")
                .addressId(addressId)
                .status("INACTIVE");

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Получаем список всех ревизий
        final MvcResult historyResult = mockMvc.perform(
                        get("/api/v1/audit/users/{id}/history", userId))
                .andExpect(status().isOk())
                .andReturn();

        final List<UserAudit> history = objectMapper.readValue(
                historyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(history).isNotEmpty();
        System.out.println("История ревизий в shouldReturnSpecificRevision:");
        history.forEach(audit -> System.out.println("Ревизия #" + audit.getRevisionNumber() +
                ", тип: " + audit.getRevisionType() +
                ", имя: " + audit.getFirstName() +
                ", фамилия: " + audit.getLastName() +
                ", статус: " + audit.getStatus()));

        final Integer firstRevisionNumber = history.get(0).getRevisionNumber();

        // Получаем конкретную ревизию (первую версию)
        final MvcResult revisionResult = mockMvc.perform(
                        get("/api/v1/audit/users/{id}/revision/{rev}", userId, firstRevisionNumber))
                .andExpect(status().isOk())
                .andReturn();

        final UserAudit revision = objectMapper.readValue(
                revisionResult.getResponse().getContentAsString(),
                UserAudit.class
        );

        System.out.println("Полученная ревизия:");
        System.out.println("Ревизия #" + revision.getRevisionNumber() +
                ", тип: " + revision.getRevisionType() +
                ", имя: " + revision.getFirstName() +
                ", фамилия: " + revision.getLastName() +
                ", статус: " + revision.getStatus());

        // Проверяем, что получили правильную версию
        assertThat(revision.getFirstName()).isEqualTo("Petr");
        assertThat(revision.getLastName()).isEqualTo("Petrov");
        assertThat(revision.getStatus()).isEqualTo("ACTIVE");
        assertThat(revision.getAddressId()).isEqualTo(addressId);
        assertThat(revision.getRevisionInstant()).isNotNull();
        assertThat(revision.getRevisionType()).isEqualTo(RevisionTypeEnum.ADD);
    }

    @Test
    void shouldReturn404ForNonExistentRevision() throws Exception {
        // Создаем пользователя
        final UserCreate userCreate = new UserCreate()
                .firstName("Alex")
                .lastName("Alexandrov")
                .addressId(addressId)
                .status("ACTIVE");

        // Сохраняем пользователя
        final MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isOk())
                .andReturn();

        // Получаем ID созданного пользователя
        final UUID userId = UUID.fromString(objectMapper.readTree(
                        createResult.getResponse().getContentAsString())
                .get("id").asText());

        // Пытаемся получить несуществующую ревизию
        mockMvc.perform(get("/api/v1/audit/users/{id}/revision/999", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/v1/audit/users/{id}/revision/1", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
} 