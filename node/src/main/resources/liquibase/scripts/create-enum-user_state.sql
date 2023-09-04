--liquibase formatted sql

-- changeset amargolina:1
CREATE TYPE user_state AS ENUM ( 'BASIC_STATE','WAIT_FOR_EMAIL_STATE','WAIT_FOR_TASK_STATE');