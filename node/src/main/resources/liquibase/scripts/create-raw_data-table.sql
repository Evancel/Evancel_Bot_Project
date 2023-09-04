--liquibase formatted sql

-- changeset amargolina:7
CREATE TABLE raw_data(
                              id SERIAL,
                              event jsonb
)