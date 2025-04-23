CREATE SCHEMA IF NOT EXISTS person;

CREATE TABLE person.countries
(
    id      BIGSERIAL PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name    VARCHAR(32),
    alpha2  VARCHAR(2),
    alpha3  VARCHAR(3),
    status  VARCHAR(32)
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE person.addresses
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created    TIMESTAMP NOT NULL,
    updated    TIMESTAMP NOT NULL,
    country_id BIGINT REFERENCES person.countries (id),
    address    VARCHAR(128),
    zip_code   VARCHAR(32),
    archived   TIMESTAMP NOT NULL,
    city       VARCHAR(32),
    state      VARCHAR(32)
);

CREATE TABLE person.users
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    secret_key  VARCHAR(32),
    created     TIMESTAMP NOT NULL,
    updated     TIMESTAMP NOT NULL,
    first_name  VARCHAR(32),
    last_name   VARCHAR(32),
    verified_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL,
    status      VARCHAR(64),
    address_id  UUID REFERENCES person.addresses (id)
);

CREATE TABLE person.individuals
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id         UUID UNIQUE REFERENCES person.users (id),
    created         TIMESTAMP NOT NULL,
    updated         TIMESTAMP NOT NULL,
    passport_number VARCHAR(32),
    phone_number    VARCHAR(32),
    email           VARCHAR(32)
);

-- Создаем индексы для оптимизации запросов
CREATE INDEX idx_addresses_country_id ON person.addresses (country_id);
CREATE INDEX idx_users_address_id ON person.users (address_id);
CREATE INDEX idx_users_status ON person.users (status);
CREATE INDEX idx_individuals_user_id ON person.individuals (user_id);
CREATE INDEX idx_individuals_email ON person.individuals (email);
CREATE INDEX idx_individuals_phone_number ON person.individuals (phone_number);