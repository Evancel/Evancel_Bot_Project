--liquibase formatted sql

-- changeset amargolina:4
CREATE TABLE app_photo (
                              id SERIAL,
                              telegram_file_id TEXT,
                              binary_content_id    BIGINT,
                              file_size BIGINT
)