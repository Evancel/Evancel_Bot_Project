--liquibase formatted sql

-- changeset amargolina:5
CREATE TABLE binary_content (
                              id SERIAL,
                              file_as_array_of_bytes bytea
)