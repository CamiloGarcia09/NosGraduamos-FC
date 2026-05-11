-- =============================================================
-- SurrealDB schema for MessageUcoLab (Phase 1: Token only)
-- =============================================================
-- Carga inicial: tablas, índices y datos semilla del flujo Token.
-- Mantiene los mismos IDs que init.sql para preservar contratos
-- entre la app y los datos existentes.
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

-- ---------- token_state ----------
DEFINE TABLE token_state SCHEMAFULL;
DEFINE FIELD name ON token_state TYPE string;
DEFINE INDEX token_state_name_idx ON token_state FIELDS name UNIQUE;

-- Seed con IDs determinísticos (mismos que en init.sql)
UPSERT token_state:`123e4567-e89b-12d3-a456-426614175000`
    CONTENT { name: 'Active' };
UPSERT token_state:`123e4567-e89b-12d3-a456-426614175001`
    CONTENT { name: 'Inactive' };

-- ---------- token ----------
DEFINE TABLE token SCHEMAFULL;
DEFINE FIELD secret_name     ON token TYPE string;
DEFINE FIELD creation_date   ON token TYPE datetime;
DEFINE FIELD expiration_date ON token TYPE datetime;
DEFINE FIELD environment_id  ON token TYPE string;
DEFINE FIELD state_id        ON token TYPE string;

DEFINE INDEX token_env_state_idx ON token FIELDS environment_id, state_id;
