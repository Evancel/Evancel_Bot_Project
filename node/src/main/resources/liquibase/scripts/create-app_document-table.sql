--liquibase formatted sql

-- changeset amargolina:3
CREATE TABLE app_document (
                          id SERIAL,
                          telegram_file_id TEXT,
                          doc_name   TEXT,
                          binary_content_id    BIGINT,
                          mime_type TEXT,
                          file_size BIGINT
)