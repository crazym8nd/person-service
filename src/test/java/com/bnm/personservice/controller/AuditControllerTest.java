package com.bnm.personservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.bnm.personservice.PersonServiceApplication;
import com.bnm.personservice.TestcontainersConfiguration;
import com.bnm.personservice.entity.Country;
import com.bnm.personservice.repository.CountryRepository;
import com.bnm.personservice.service.AuditService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AuditControllerTest {

  @Autowired
  private CountryRepository countryRepository;

  @Autowired
  private AuditService auditService;

  @Test
  void shouldTrackCountryChanges() {
    // Создаем страну
    final Country country = new Country();
    country.setName("Russia");
    country.setAlpha2("RU");
    country.setAlpha3("RUS");
    country.setStatus("ACTIVE");

    final Country savedCountry = countryRepository.save(country);

    // Изменяем статус
    savedCountry.setStatus("INACTIVE");
    countryRepository.save(savedCountry);

    // Получаем историю изменений
    final List<Country> revisions = auditService.getRevisions(Country.class, savedCountry.getId());

    // Проверяем, что есть две версии
    assertThat(revisions).hasSize(2);

    // Проверяем первую версию
    final Country firstRevision = revisions.get(0);
    assertThat(firstRevision.getName()).isEqualTo("Russia");
    assertThat(firstRevision.getStatus()).isEqualTo("ACTIVE");

    // Проверяем вторую версию
    final Country secondRevision = revisions.get(1);
    assertThat(secondRevision.getName()).isEqualTo("Russia");
    assertThat(secondRevision.getStatus()).isEqualTo("INACTIVE");
  }
} 