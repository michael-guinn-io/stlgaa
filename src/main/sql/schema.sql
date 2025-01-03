CREATE TABLE IF NOT EXISTS Profile
(
    id      BINARY(16)   NOT NULL,
    version BIGINT NOT NULL,
    name    VARCHAR(255) NULL,
    CONSTRAINT pk_profile PRIMARY KEY (id)
);