-- =============================================================
-- SurrealDB schema for MessageUcoLab
-- =============================================================
-- Equivalent bootstrap for deployment/docker/scripts/init.sql.
-- Tables use the application collection names (without the SQL
-- _data suffix) so the app can run with SurrealDB only.
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

-- ---------- language_base ----------
DEFINE TABLE language_base SCHEMAFULL;
DEFINE FIELD language ON language_base TYPE string;
DEFINE FIELD code     ON language_base TYPE string;
DEFINE INDEX language_base_code_idx ON language_base FIELDS code UNIQUE;

CREATE type::thing("language_base", rand::uuid()) CONTENT { language: 'English', code: 'EN' };
CREATE type::thing("language_base", rand::uuid()) CONTENT { language: 'Spanish', code: 'ES' };
CREATE type::thing("language_base", rand::uuid()) CONTENT { language: 'French',  code: 'FR' };

-- ---------- application_state ----------
DEFINE TABLE application_state SCHEMAFULL;
DEFINE FIELD name ON application_state TYPE string;
DEFINE INDEX application_state_name_idx ON application_state FIELDS name UNIQUE;

CREATE type::thing("application_state", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("application_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- application ----------
DEFINE TABLE application SCHEMAFULL;
DEFINE FIELD name        ON application TYPE string;
DEFINE FIELD language_id ON application TYPE string;
DEFINE FIELD start_date  ON application TYPE datetime;
DEFINE FIELD end_date    ON application TYPE datetime;
DEFINE FIELD state_id    ON application TYPE string;
DEFINE INDEX application_name_idx ON application FIELDS name;
DEFINE INDEX application_language_state_idx ON application FIELDS language_id, state_id;

-- ---------- environment_type ----------
DEFINE TABLE environment_type SCHEMAFULL;
DEFINE FIELD name ON environment_type TYPE string;
DEFINE INDEX environment_type_name_idx ON environment_type FIELDS name UNIQUE;

CREATE type::thing("environment_type", rand::uuid()) CONTENT { name: 'Develop' };
CREATE type::thing("environment_type", rand::uuid()) CONTENT { name: 'Production' };
CREATE type::thing("environment_type", rand::uuid()) CONTENT { name: 'Testing' };

-- ---------- environment_state ----------
DEFINE TABLE environment_state SCHEMAFULL;
DEFINE FIELD name ON environment_state TYPE string;
DEFINE INDEX environment_state_name_idx ON environment_state FIELDS name UNIQUE;

CREATE type::thing("environment_state", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("environment_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- environment ----------
DEFINE TABLE environment SCHEMAFULL;
DEFINE FIELD name           ON environment TYPE string;
DEFINE FIELD application_id ON environment TYPE string;
DEFINE FIELD type_id        ON environment TYPE string;
DEFINE FIELD state_id       ON environment TYPE string;
DEFINE INDEX environment_application_idx ON environment FIELDS application_id;
DEFINE INDEX environment_type_state_idx ON environment FIELDS type_id, state_id;

-- ---------- functionality_state ----------
DEFINE TABLE functionality_state SCHEMAFULL;
DEFINE FIELD name ON functionality_state TYPE string;
DEFINE INDEX functionality_state_name_idx ON functionality_state FIELDS name UNIQUE;

CREATE type::thing("functionality_state", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("functionality_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- functionality ----------
DEFINE TABLE functionality SCHEMAFULL;
DEFINE FIELD name           ON functionality TYPE string;
DEFINE FIELD application_id ON functionality TYPE string;
DEFINE FIELD start_date     ON functionality TYPE datetime;
DEFINE FIELD end_date       ON functionality TYPE datetime;
DEFINE FIELD state_id       ON functionality TYPE string;
DEFINE INDEX functionality_application_idx ON functionality FIELDS application_id;
DEFINE INDEX functionality_state_idx ON functionality FIELDS state_id;

-- ---------- message_category ----------
DEFINE TABLE message_category SCHEMAFULL;
DEFINE FIELD name ON message_category TYPE string;
DEFINE INDEX message_category_name_idx ON message_category FIELDS name UNIQUE;

CREATE type::thing("message_category", rand::uuid()) CONTENT { name: 'Error' };
CREATE type::thing("message_category", rand::uuid()) CONTENT { name: 'Information' };
CREATE type::thing("message_category", rand::uuid()) CONTENT { name: 'Confirmation' };
CREATE type::thing("message_category", rand::uuid()) CONTENT { name: 'Warning' };
CREATE type::thing("message_category", rand::uuid()) CONTENT { name: 'Debug' };

-- ---------- message_type ----------
DEFINE TABLE message_type SCHEMAFULL;
DEFINE FIELD name ON message_type TYPE string;
DEFINE INDEX message_type_name_idx ON message_type FIELDS name UNIQUE;

CREATE type::thing("message_type", rand::uuid()) CONTENT { name: 'Functional' };
CREATE type::thing("message_type", rand::uuid()) CONTENT { name: 'Technical' };

-- ---------- message_state ----------
DEFINE TABLE message_state SCHEMAFULL;
DEFINE FIELD name ON message_state TYPE string;
DEFINE INDEX message_state_name_idx ON message_state FIELDS name UNIQUE;

CREATE type::thing("message_state", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("message_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- message ----------
DEFINE TABLE message SCHEMAFULL;
DEFINE FIELD code             ON message TYPE string;
DEFINE FIELD title            ON message TYPE string;
DEFINE FIELD content          ON message TYPE string;
DEFINE FIELD type_id          ON message TYPE string;
DEFINE FIELD category_id      ON message TYPE string;
DEFINE FIELD status_id        ON message TYPE string;
DEFINE FIELD application      ON message TYPE string;
DEFINE FIELD functionality_id ON message TYPE string;
DEFINE INDEX message_code_idx ON message FIELDS code;
DEFINE INDEX message_catalogs_idx ON message FIELDS type_id, category_id, status_id;
DEFINE INDEX message_functionality_idx ON message FIELDS functionality_id;

-- ---------- message_environment_state ----------
DEFINE TABLE message_environment_state SCHEMAFULL;
DEFINE FIELD name ON message_environment_state TYPE string;
DEFINE INDEX message_environment_state_name_idx ON message_environment_state FIELDS name UNIQUE;

CREATE type::thing("message_environment_state", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("message_environment_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- Alias used by existing Mongo collection constants.
DEFINE TABLE status_message_environment SCHEMAFULL;
DEFINE FIELD name ON status_message_environment TYPE string;
DEFINE INDEX status_message_environment_name_idx ON status_message_environment FIELDS name UNIQUE;

CREATE type::thing("status_message_environment", rand::uuid()) CONTENT { name: 'Active' };
CREATE type::thing("status_message_environment", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- message_environment ----------
DEFINE TABLE message_environment SCHEMAFULL;
DEFINE FIELD message_id    ON message_environment TYPE string;
DEFINE FIELD environment_id ON message_environment TYPE string;
DEFINE FIELD state_data_id ON message_environment TYPE string;
DEFINE INDEX message_environment_message_idx ON message_environment FIELDS message_id;
DEFINE INDEX message_environment_env_state_idx ON message_environment FIELDS environment_id, state_data_id;

-- ---------- parameter ----------
DEFINE TABLE parameter SCHEMAFULL;
DEFINE FIELD message_id  ON parameter TYPE string;
DEFINE FIELD name        ON parameter TYPE string;
DEFINE FIELD description ON parameter TYPE string;
DEFINE INDEX parameter_message_idx ON parameter FIELDS message_id;

-- ---------- represent_parameter ----------
DEFINE TABLE represent_parameter SCHEMAFULL;
DEFINE FIELD start             ON represent_parameter TYPE string;
DEFINE FIELD end_value         ON represent_parameter TYPE string;
DEFINE FIELD end               ON represent_parameter TYPE string;
DEFINE FIELD application_id    ON represent_parameter TYPE string;
DEFINE FIELD default_parameter ON represent_parameter TYPE bool;
DEFINE FIELD parameter         ON represent_parameter TYPE bool;
DEFINE INDEX represent_parameter_application_idx ON represent_parameter FIELDS application_id;

-- ---------- token_state ----------
DEFINE TABLE token_state SCHEMAFULL;
DEFINE FIELD name ON token_state TYPE string;
DEFINE INDEX token_state_name_idx ON token_state FIELDS name UNIQUE;

UPSERT token_state:`123e4567-e89b-12d3-a456-426614175000` CONTENT { name: 'Active' };
CREATE type::thing("token_state", rand::uuid()) CONTENT { name: 'Inactive' };

-- ---------- token ----------
DEFINE TABLE token SCHEMAFULL;
DEFINE FIELD secret_name     ON token TYPE string;
DEFINE FIELD creation_date   ON token TYPE datetime;
DEFINE FIELD expiration_date ON token TYPE datetime;
DEFINE FIELD environment_id  ON token TYPE string;
DEFINE FIELD state_id        ON token TYPE string;
DEFINE INDEX token_env_state_idx ON token FIELDS environment_id, state_id;
