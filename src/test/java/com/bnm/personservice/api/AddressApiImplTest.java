package com.bnm.personservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.model.Address;
import com.bnm.personservice.model.AddressCreate;
import com.bnm.personservice.model.Country;
import com.bnm.personservice.model.CountryCreate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
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
class AddressApiImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JsonNullableModule());
    }

    private Country createTestCountry() throws Exception {
        final CountryCreate countryCreate = new CountryCreate()
            .name("Russia")
            .alpha2("RU")
            .alpha3("RUS")
            .status("ACTIVE");

        final MvcResult mvcResult = mockMvc.perform(post("/api/v1/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(countryCreate)))
            .andExpect(status().isOk())
            .andReturn();

        return objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            Country.class
        );
    }

    @Test
    void createAddress_shouldCreateNewAddress() throws Exception {
        // given
        final Country country = createTestCountry();
        
        final AddressCreate addressCreate = new AddressCreate()
            .countryId(country.getId())
            .address("1 Lenin Street")
            .zipCode("123456")
            .city("Moscow")
            .state("Moscow Region");

        // when
        final MvcResult mvcResult = mockMvc.perform(post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressCreate)))
            .andExpect(status().isOk())
            .andReturn();

        // then
        final Address createdAddress = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            Address.class
        );

        assertThat(createdAddress.getId()).isNotNull();
        assertThat(createdAddress.getCountryId()).isEqualTo(country.getId());
        assertThat(createdAddress.getAddress()).isEqualTo(addressCreate.getAddress());
        assertThat(createdAddress.getZipCode()).isEqualTo(addressCreate.getZipCode());
        assertThat(createdAddress.getCity()).isEqualTo(addressCreate.getCity());
        assertThat(createdAddress.getState()).isEqualTo(addressCreate.getState());
        assertThat(createdAddress.getCreated()).isNotNull()
            .isBefore(OffsetDateTime.now());
        assertThat(createdAddress.getUpdated()).isNotNull()
            .isBefore(OffsetDateTime.now());
    }

    @Test
    void getAddresses_shouldReturnListOfAddresses() throws Exception {
        // given
        final Country country = createTestCountry();
        
        final AddressCreate addressCreate = new AddressCreate()
            .countryId(country.getId())
            .address("10 Pushkin Street")
            .zipCode("654321")
            .city("Saint Petersburg")
            .state("Leningrad Region");

        // Create test address
        mockMvc.perform(post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressCreate)))
            .andExpect(status().isOk());

        // when
        final MvcResult mvcResult = mockMvc.perform(get("/api/v1/addresses"))
            .andExpect(status().isOk())
            .andReturn();

        // then
        final List<Address> addresses = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            new TypeReference<>() {}
        );

        assertThat(addresses).hasSize(1);
        final Address address = addresses.get(0);
        assertThat(address.getId()).isNotNull();
        assertThat(address.getCountryId()).isEqualTo(country.getId());
        assertThat(address.getAddress()).isEqualTo(addressCreate.getAddress());
        assertThat(address.getZipCode()).isEqualTo(addressCreate.getZipCode());
        assertThat(address.getCity()).isEqualTo(addressCreate.getCity());
        assertThat(address.getState()).isEqualTo(addressCreate.getState());
    }

    @Test
    void getAddressById_shouldReturnAddress() throws Exception {
        // given
        final Country country = createTestCountry();
        
        final AddressCreate addressCreate = new AddressCreate()
            .countryId(country.getId())
            .address("15 Peace Avenue")
            .zipCode("789012")
            .city("Kazan")
            .state("Tatarstan");

        // Create test address
        final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressCreate)))
            .andExpect(status().isOk())
            .andReturn();

        final Address createdAddress = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            Address.class
        );

        // when
        final MvcResult mvcResult = mockMvc.perform(
                get("/api/v1/addresses/{id}", createdAddress.getId()))
            .andExpect(status().isOk())
            .andReturn();

        // then
        final Address address = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            Address.class
        );

        assertThat(address.getId()).isEqualTo(createdAddress.getId());
        assertThat(address.getCountryId()).isEqualTo(country.getId());
        assertThat(address.getAddress()).isEqualTo(addressCreate.getAddress());
        assertThat(address.getZipCode()).isEqualTo(addressCreate.getZipCode());
        assertThat(address.getCity()).isEqualTo(addressCreate.getCity());
        assertThat(address.getState()).isEqualTo(addressCreate.getState());
    }

    @Test
    void updateAddress_shouldUpdateExistingAddress() throws Exception {
        // given
        final Country country = createTestCountry();
        
        final AddressCreate addressCreate = new AddressCreate()
            .countryId(country.getId())
            .address("5 Gagarin Street")
            .zipCode("345678")
            .city("Novosibirsk")
            .state("Novosibirsk Region");

        // Create test address
        final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressCreate)))
            .andExpect(status().isOk())
            .andReturn();

        final Address createdAddress = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            Address.class
        );

        final AddressCreate updateRequest = new AddressCreate()
            .countryId(country.getId())
            .address("7 Gagarin Street")
            .zipCode("345678")
            .city("Novosibirsk")
            .state("Novosibirsk Region");

        // when
        final MvcResult mvcResult = mockMvc.perform(
                put("/api/v1/addresses/{id}", createdAddress.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andReturn();

        // then
        final Address updatedAddress = objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            Address.class
        );

        assertThat(updatedAddress.getId()).isEqualTo(createdAddress.getId());
        assertThat(updatedAddress.getCountryId()).isEqualTo(country.getId());
        assertThat(updatedAddress.getAddress()).isEqualTo(updateRequest.getAddress());
        assertThat(updatedAddress.getZipCode()).isEqualTo(updateRequest.getZipCode());
        assertThat(updatedAddress.getCity()).isEqualTo(updateRequest.getCity());
        assertThat(updatedAddress.getState()).isEqualTo(updateRequest.getState());
        assertThat(updatedAddress.getUpdated()).isAfter(createdAddress.getUpdated());
    }

    @Test
    void deleteAddress_shouldDeleteExistingAddress() throws Exception {
        // given
        final Country country = createTestCountry();
        
        final AddressCreate addressCreate = new AddressCreate()
            .countryId(country.getId())
            .address("20 Kirov Street")
            .zipCode("901234")
            .city("Yekaterinburg")
            .state("Sverdlovsk Region");

        // Create test address
        final MvcResult createResult = mockMvc.perform(post("/api/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressCreate)))
            .andExpect(status().isOk())
            .andReturn();

        final Address createdAddress = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            Address.class
        );

        // when
        mockMvc.perform(delete("/api/v1/addresses/{id}", createdAddress.getId()))
            .andExpect(status().isNoContent());

        // then
        mockMvc.perform(get("/api/v1/addresses/{id}", createdAddress.getId()))
            .andExpect(status().isNotFound());
    }
} 