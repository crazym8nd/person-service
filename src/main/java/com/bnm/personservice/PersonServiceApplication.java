package com.bnm.personservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.bnm.personservice.repository")
public class PersonServiceApplication {

  public static void main(final String[] args) {
    SpringApplication.run(PersonServiceApplication.class, args);
  }

}
