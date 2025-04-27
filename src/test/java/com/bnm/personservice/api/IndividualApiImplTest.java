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
import com.bnm.personservice.model.Individual;
import com.bnm.personservice.model.IndividualCreate;
import com.bnm.personservice.model.User;
import com.bnm.personservice.model.UserCreate;
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
class IndividualApiImplTest {

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
        .name("United States")
        .alpha2("US")
        .alpha3("USA")
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

  private Address createTestAddress(final Country country) throws Exception {
    final AddressCreate addressCreate = new AddressCreate()
        .countryId(country.getId())
        .address("123 Main Street")
        .zipCode("12345")
        .city("New York")
        .state("NY");

    final MvcResult mvcResult = mockMvc.perform(post("/api/v1/addresses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressCreate)))
        .andExpect(status().isOk())
        .andReturn();

    return objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        Address.class
    );
  }

  private User createTestUser(final Address address) throws Exception {
    final UserCreate userCreate = new UserCreate()
        .firstName("John")
        .lastName("Doe")
        .status("ACTIVE")
        .addressId(address.getId());

    final MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userCreate)))
        .andExpect(status().isOk())
        .andReturn();

    return objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        User.class
    );
  }

  @Test
  void createIndividual_shouldCreateNewIndividual() throws Exception {
    // given
    final Country country = createTestCountry();
    final Address address = createTestAddress(country);
    final User user = createTestUser(address);

    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("123456789")
        .phoneNumber("+1234567890")
        .email("john.doe@example.com");

    // when
    final MvcResult mvcResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    // then
    final Individual createdIndividual = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        Individual.class
    );

    assertThat(createdIndividual.getId()).isNotNull();
    assertThat(createdIndividual.getUserId()).isEqualTo(user.getId());
    assertThat(createdIndividual.getPassportNumber()).isEqualTo(
        individualCreate.getPassportNumber());
    assertThat(createdIndividual.getPhoneNumber()).isEqualTo(individualCreate.getPhoneNumber());
    assertThat(createdIndividual.getEmail()).isEqualTo(individualCreate.getEmail());
    assertThat(createdIndividual.getCreated()).isNotNull()
        .isBefore(OffsetDateTime.now());
    assertThat(createdIndividual.getUpdated()).isNotNull()
        .isBefore(OffsetDateTime.now());
  }

  @Test
  void getIndividuals_shouldReturnListOfIndividuals() throws Exception {
    // given
    final Country country = createTestCountry();
    final Address address = createTestAddress(country);
    final User user = createTestUser(address);

    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("987654321")
        .phoneNumber("+9876543210")
        .email("jane.smith@example.com");

    // Create test individual
    mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk());

    // when
    final MvcResult mvcResult = mockMvc.perform(get("/api/v1/individuals"))
        .andExpect(status().isOk())
        .andReturn();

    // then
    final List<Individual> individuals = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        new TypeReference<>() {
        }
    );

    assertThat(individuals).hasSize(1);
    final Individual individual = individuals.get(0);
    assertThat(individual.getId()).isNotNull();
    assertThat(individual.getUserId()).isEqualTo(user.getId());
    assertThat(individual.getPassportNumber()).isEqualTo(individualCreate.getPassportNumber());
    assertThat(individual.getPhoneNumber()).isEqualTo(individualCreate.getPhoneNumber());
    assertThat(individual.getEmail()).isEqualTo(individualCreate.getEmail());
  }

  @Test
  void getIndividualById_shouldReturnIndividual() throws Exception {
    // given
    final Country country = createTestCountry();
    final Address address = createTestAddress(country);
    final User user = createTestUser(address);

    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("456789123")
        .phoneNumber("+4567891230")
        .email("robert.johnson@example.com");

    // Create test individual
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    final Individual createdIndividual = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        Individual.class
    );

    // when
    final MvcResult mvcResult = mockMvc.perform(
            get("/api/v1/individuals/{id}", createdIndividual.getId()))
        .andExpect(status().isOk())
        .andReturn();

    // then
    final Individual individual = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        Individual.class
    );

    assertThat(individual.getId()).isEqualTo(createdIndividual.getId());
    assertThat(individual.getUserId()).isEqualTo(user.getId());
    assertThat(individual.getPassportNumber()).isEqualTo(individualCreate.getPassportNumber());
    assertThat(individual.getPhoneNumber()).isEqualTo(individualCreate.getPhoneNumber());
    assertThat(individual.getEmail()).isEqualTo(individualCreate.getEmail());
  }

  @Test
  void updateIndividual_shouldUpdateExistingIndividual() throws Exception {
    // given
    final Country country = createTestCountry();
    final Address address = createTestAddress(country);
    final User user = createTestUser(address);

    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("789123456")
        .phoneNumber("+7891234560")
        .email("michael.brown@example.com");

    // Create test individual
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    final Individual createdIndividual = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        Individual.class
    );

    final IndividualCreate updateRequest = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("789123456")
        .phoneNumber("+7891234561")
        .email("mike.brown@example.com");

    // when
    final MvcResult mvcResult = mockMvc.perform(
            put("/api/v1/individuals/{id}", createdIndividual.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andReturn();

    // then
    final Individual updatedIndividual = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        Individual.class
    );

    assertThat(updatedIndividual.getId()).isEqualTo(createdIndividual.getId());
    assertThat(updatedIndividual.getUserId()).isEqualTo(user.getId());
    assertThat(updatedIndividual.getPassportNumber()).isEqualTo(updateRequest.getPassportNumber());
    assertThat(updatedIndividual.getPhoneNumber()).isEqualTo(updateRequest.getPhoneNumber());
    assertThat(updatedIndividual.getEmail()).isEqualTo(updateRequest.getEmail());
    assertThat(updatedIndividual.getUpdated()).isAfter(createdIndividual.getUpdated());
  }

  @Test
  void deleteIndividual_shouldDeleteExistingIndividual() throws Exception {
    // given
    final Country country = createTestCountry();
    final Address address = createTestAddress(country);
    final User user = createTestUser(address);

    final IndividualCreate individualCreate = new IndividualCreate()
        .userId(user.getId())
        .passportNumber("321654987")
        .phoneNumber("+3216549870")
        .email("william.davis@example.com");

    // Create test individual
    final MvcResult createResult = mockMvc.perform(post("/api/v1/individuals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(individualCreate)))
        .andExpect(status().isOk())
        .andReturn();

    final Individual createdIndividual = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        Individual.class
    );

    // when
    mockMvc.perform(delete("/api/v1/individuals/{id}", createdIndividual.getId()))
        .andExpect(status().isNoContent());

    // then
    mockMvc.perform(get("/api/v1/individuals/{id}", createdIndividual.getId()))
        .andExpect(status().isNotFound());
  }
} 