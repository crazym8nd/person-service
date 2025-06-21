package com.bnm.personservice.controller;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.*;
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
class IndividualAuditControllerTest {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String RUSSIA_NAME = "Russia";
    private static final String RUSSIA_ALPHA2 = "RU";
    private static final String RUSSIA_ALPHA3 = "RUS";
    private static final String ADDRESS = "Lenina st., 1";
    private static final String ZIP_CODE = "123456";
    private static final String CITY = "Moscow";
    private static final String STATE = "Moscow";
    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanov";
    private static final String PASSPORT_NUMBER_1 = "1234 567890";
    private static final String PASSPORT_NUMBER_2 = "9876 543210";
    private static final String PASSPORT_NUMBER_3 = "5555 555555";
    private static final String PHONE_NUMBER_1 = "+7 (999) 123-45-67";
    private static final String PHONE_NUMBER_2 = "+7 (999) 999-99-99";
    private static final String EMAIL_1 = "ivan@example.com";
    private static final String EMAIL_2 = "ivan.ivanov@example.com";
    private static final String EMAIL_3 = "petr@example.com";
    private static final String EMAIL_4 = "petr.petrov@example.com";
    private static final String EMAIL_5 = "alex@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем страну
        final CountryCreate countryCreate = new CountryCreate()
                .name(RUSSIA_NAME)
                .alpha2(RUSSIA_ALPHA2)
                .alpha3(RUSSIA_ALPHA3)
                .status(STATUS_ACTIVE);

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
                .address(ADDRESS)
                .zipCode(ZIP_CODE)
                .city(CITY)
                .state(STATE);

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
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .addressId(addressId)
                .status(STATUS_ACTIVE);

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
                .passportNumber(PASSPORT_NUMBER_1)
                .phoneNumber(PHONE_NUMBER_1)
                .email(EMAIL_1);

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
                .passportNumber(PASSPORT_NUMBER_1)
                .phoneNumber(PHONE_NUMBER_2)
                .email(EMAIL_2);

        mockMvc.perform(put("/api/v1/individuals/{id}", individualId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Получаем историю изменений
        final MvcResult historyResult = mockMvc.perform(
                        get("/api/v1/audit/individuals/{id}/history", individualId))
                .andExpect(status().isOk())
                .andReturn();

        final List<IndividualAudit> history = objectMapper.readValue(
                historyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Проверяем, что есть две версии
        assertThat(history).hasSize(2);

        // Проверяем первую версию
        final IndividualAudit firstVersion = history.get(0);
        assertThat(firstVersion.getPassportNumber()).isEqualTo(PASSPORT_NUMBER_1);
        assertThat(firstVersion.getPhoneNumber()).isEqualTo(PHONE_NUMBER_1);
        assertThat(firstVersion.getEmail()).isEqualTo(EMAIL_1);
        assertThat(firstVersion.getUserId()).isEqualTo(userId);
        assertThat(firstVersion.getRevisionInstant()).isNotNull();
        assertThat(firstVersion.getRevisionType()).isEqualTo(IndividualAudit.RevisionTypeEnum.ADD);

        // Проверяем вторую версию
        final IndividualAudit secondVersion = history.get(1);
        assertThat(secondVersion.getPassportNumber()).isEqualTo(PASSPORT_NUMBER_1);
        assertThat(secondVersion.getPhoneNumber()).isEqualTo(PHONE_NUMBER_2);
        assertThat(secondVersion.getEmail()).isEqualTo(EMAIL_2);
        assertThat(secondVersion.getUserId()).isEqualTo(userId);
        assertThat(secondVersion.getRevisionInstant()).isNotNull();
        assertThat(secondVersion.getRevisionType()).isEqualTo(IndividualAudit.RevisionTypeEnum.MOD);
    }

    @Test
    void shouldReturnSpecificRevision() throws Exception {
        // Создаем физическое лицо
        final IndividualCreate individualCreate = new IndividualCreate()
                .userId(userId)
                .passportNumber(PASSPORT_NUMBER_2)
                .phoneNumber(PHONE_NUMBER_1)
                .email(EMAIL_3);

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
                .passportNumber(PASSPORT_NUMBER_2)
                .phoneNumber(PHONE_NUMBER_2)
                .email(EMAIL_4);

        mockMvc.perform(put("/api/v1/individuals/{id}", individualId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Получаем список всех ревизий
        final MvcResult historyResult = mockMvc.perform(
                        get("/api/v1/audit/individuals/{id}/history", individualId))
                .andExpect(status().isOk())
                .andReturn();

        final List<IndividualAudit> history = objectMapper.readValue(
                historyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(history).isNotEmpty();
        System.out.println("История ревизий:");
        history.forEach(audit -> System.out.println("Ревизия #" + audit.getRevisionNumber() +
                ", тип: " + audit.getRevisionType() +
                ", время: " + audit.getRevisionInstant()));

        final Integer firstRevisionNumber = history.get(0).getRevisionNumber();
        System.out.println("Запрашиваем ревизию #" + firstRevisionNumber);

        // Получаем конкретную ревизию (первую версию)
        final MvcResult revisionResult = mockMvc.perform(
                        get("/api/v1/audit/individuals/{id}/revision/{rev}", individualId, firstRevisionNumber))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Ответ сервера при запросе ревизии:");
        System.out.println(revisionResult.getResponse().getContentAsString());

        final IndividualAudit revision = objectMapper.readValue(
                revisionResult.getResponse().getContentAsString(),
                IndividualAudit.class
        );

        // Проверяем, что получили правильную версию
        assertThat(revision.getPassportNumber()).isEqualTo(PASSPORT_NUMBER_2);
        assertThat(revision.getPhoneNumber()).isEqualTo(PHONE_NUMBER_1);
        assertThat(revision.getEmail()).isEqualTo(EMAIL_3);
        assertThat(revision.getUserId()).isEqualTo(userId);
        assertThat(revision.getRevisionInstant()).isNotNull();
        assertThat(revision.getRevisionType()).isEqualTo(IndividualAudit.RevisionTypeEnum.ADD);
    }

    @Test
    void shouldReturn404ForNonExistentRevision() throws Exception {
        // Создаем физическое лицо
        final IndividualCreate individualCreate = new IndividualCreate()
                .userId(userId)
                .passportNumber(PASSPORT_NUMBER_3)
                .phoneNumber(PHONE_NUMBER_1)
                .email(EMAIL_5);

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