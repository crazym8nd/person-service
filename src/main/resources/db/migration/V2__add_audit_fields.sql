-- Rename existing audit columns
ALTER TABLE person.countries
    RENAME COLUMN created TO created_at;
ALTER TABLE person.countries
    RENAME COLUMN updated TO updated_at;

-- Add new audit columns to countries
ALTER TABLE person.countries
    ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system';

-- Update addresses table
ALTER TABLE person.addresses
    RENAME COLUMN created TO created_at;
ALTER TABLE person.addresses
    RENAME COLUMN updated TO updated_at;
ALTER TABLE person.addresses
    ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system';

-- Update users table
ALTER TABLE person.users
    RENAME COLUMN created TO created_at;
ALTER TABLE person.users
    RENAME COLUMN updated TO updated_at;
ALTER TABLE person.users
    ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system';

-- Update individuals table
ALTER TABLE person.individuals
    RENAME COLUMN created TO created_at;
ALTER TABLE person.individuals
    RENAME COLUMN updated TO updated_at;
ALTER TABLE person.individuals
    ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(255) NOT NULL DEFAULT 'system'; 