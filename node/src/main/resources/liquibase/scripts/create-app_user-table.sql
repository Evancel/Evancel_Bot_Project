--liquibase formatted sql

-- changeset amargolina:2
CREATE TABLE app_user (
                          id SERIAL,
                          telegram_user_id BIGINT,
                          first_login_date TIMESTAMP,
                          first_name   TEXT,
                          last_name    TEXT,
                          user_name    TEXT,
                          email        TEXT,
                          is_active    BOOLEAN,
                          state   TEXT
)