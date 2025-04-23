CREATE SCHEMA IF NOT EXISTS person;

CREATE TABLE person.countries (
    id SERIAL PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(32) NOT NULL,
    alpha2 VARCHAR(2) NOT NULL,
    alpha3 VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE person.addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    country_id INTEGER REFERENCES person.countries (id),
    address VARCHAR(128) NOT NULL,
    zip_code VARCHAR(32) NOT NULL,
    archived TIMESTAMP,
    city VARCHAR(32) NOT NULL,
    state VARCHAR(32)
);

CREATE TABLE person.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    secret_key VARCHAR(32),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    verified_at TIMESTAMP,
    archived_at TIMESTAMP,
    status VARCHAR(64) NOT NULL,
    address_id UUID REFERENCES person.addresses(id)
);

CREATE TABLE person.individuals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES person.users(id),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    passport_number VARCHAR(32),
    phone_number VARCHAR(32),
    email VARCHAR(32)
); 