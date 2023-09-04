--liquibase formatted sql

-- changeset amargolina:8
CREATE UNIQUE INDEX email_idx ON app_user(email);