CREATE TABLE person.addresses_aud
(
    id         UUID    NOT NULL,
    rev        INTEGER NOT NULL,
    revtype    SMALLINT,
    created    TIMESTAMP WITHOUT TIME ZONE,
    updated    TIMESTAMP WITHOUT TIME ZONE,
    country_id BIGINT,
    address    VARCHAR(255),
    zip_code   VARCHAR(255),
    archived   TIMESTAMP WITHOUT TIME ZONE,
    city       VARCHAR(255),
    state      VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255),
    CONSTRAINT addresses_aud_pkey PRIMARY KEY (id, rev),
    CONSTRAINT addresses_aud_rev_fk FOREIGN KEY (rev) REFERENCES person.revinfo (rev)
); 