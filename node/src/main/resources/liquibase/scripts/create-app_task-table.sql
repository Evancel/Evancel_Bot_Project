--liquibase formatted sql

-- changeset amargolina:6
CREATE TABLE app_task (
                           id SERIAL,
                           chat_id BIGINT,
                           task TEXT,
                           date_time TIMESTAMP
)