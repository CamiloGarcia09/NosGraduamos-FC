-- =============================================================
-- SurrealDB schema for MessageUcoLab
-- =============================================================
-- This file is intentionally limited to schema and stable domain
-- catalogs. Development/demo records live in surreal-seed.dev.surql.
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

-- ---------- language_base ----------
DEFINE TABLE IF NOT EXISTS language_base SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS language ON language_base TYPE string;
DEFINE FIELD IF NOT EXISTS code     ON language_base TYPE string;
DEFINE INDEX IF NOT EXISTS language_base_code_idx ON language_base FIELDS code UNIQUE;

UPSERT language_base:en CONTENT { language: 'English', code: 'EN' };
UPSERT language_base:es CONTENT { language: 'Spanish', code: 'ES' };
UPSERT language_base:fr CONTENT { language: 'French',  code: 'FR' };

-- ---------- application_state ----------
DEFINE TABLE IF NOT EXISTS application_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON application_state TYPE string;
DEFINE INDEX IF NOT EXISTS application_state_name_idx ON application_state FIELDS name UNIQUE;

UPSERT application_state:active CONTENT { name: 'Active' };
UPSERT application_state:inactive CONTENT { name: 'Inactive' };

-- ---------- application ----------
DEFINE TABLE IF NOT EXISTS application SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name        ON application TYPE string;
DEFINE FIELD IF NOT EXISTS seed_key    ON application TYPE option<string>;
DEFINE FIELD IF NOT EXISTS language_id ON application TYPE record<language_base> | string;
DEFINE FIELD IF NOT EXISTS start_date  ON application TYPE datetime;
DEFINE FIELD IF NOT EXISTS end_date    ON application TYPE datetime;
DEFINE FIELD IF NOT EXISTS state_id    ON application TYPE record<application_state> | string;
DEFINE INDEX IF NOT EXISTS application_name_idx ON application FIELDS name;
DEFINE INDEX IF NOT EXISTS application_language_state_idx ON application FIELDS language_id, state_id;

-- ---------- environment_type ----------
DEFINE TABLE IF NOT EXISTS environment_type SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON environment_type TYPE string;
DEFINE INDEX IF NOT EXISTS environment_type_name_idx ON environment_type FIELDS name UNIQUE;

UPSERT environment_type:develop CONTENT { name: 'Develop' };
UPSERT environment_type:production CONTENT { name: 'Production' };
UPSERT environment_type:testing CONTENT { name: 'Testing' };

-- ---------- environment_state ----------
DEFINE TABLE IF NOT EXISTS environment_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON environment_state TYPE string;
DEFINE INDEX IF NOT EXISTS environment_state_name_idx ON environment_state FIELDS name UNIQUE;

UPSERT environment_state:active CONTENT { name: 'Active' };
UPSERT environment_state:inactive CONTENT { name: 'Inactive' };

-- ---------- environment ----------
DEFINE TABLE IF NOT EXISTS environment SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name           ON environment TYPE string;
DEFINE FIELD IF NOT EXISTS seed_key       ON environment TYPE option<string>;
DEFINE FIELD IF NOT EXISTS application_id ON environment TYPE record<application> | string;
DEFINE FIELD IF NOT EXISTS type_id        ON environment TYPE record<environment_type> | string;
DEFINE FIELD IF NOT EXISTS state_id       ON environment TYPE record<environment_state> | string;
DEFINE INDEX IF NOT EXISTS environment_application_idx ON environment FIELDS application_id;
DEFINE INDEX IF NOT EXISTS environment_type_state_idx ON environment FIELDS type_id, state_id;

-- ---------- functionality_state ----------
DEFINE TABLE IF NOT EXISTS functionality_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON functionality_state TYPE string;
DEFINE INDEX IF NOT EXISTS functionality_state_name_idx ON functionality_state FIELDS name UNIQUE;

UPSERT functionality_state:active CONTENT { name: 'Active' };
UPSERT functionality_state:inactive CONTENT { name: 'Inactive' };

-- ---------- functionality ----------
DEFINE TABLE IF NOT EXISTS functionality SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name           ON functionality TYPE string;
DEFINE FIELD IF NOT EXISTS seed_key       ON functionality TYPE option<string>;
DEFINE FIELD IF NOT EXISTS application_id ON functionality TYPE record<application> | string;
DEFINE FIELD IF NOT EXISTS start_date     ON functionality TYPE datetime;
DEFINE FIELD IF NOT EXISTS end_date       ON functionality TYPE datetime;
DEFINE FIELD IF NOT EXISTS state_id       ON functionality TYPE record<functionality_state> | string;
DEFINE INDEX IF NOT EXISTS functionality_application_idx ON functionality FIELDS application_id;
DEFINE INDEX IF NOT EXISTS functionality_state_idx ON functionality FIELDS state_id;

-- ---------- message_category ----------
DEFINE TABLE IF NOT EXISTS message_category SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON message_category TYPE string;
DEFINE INDEX IF NOT EXISTS message_category_name_idx ON message_category FIELDS name UNIQUE;

UPSERT message_category:error CONTENT { name: 'Error' };
UPSERT message_category:information CONTENT { name: 'Information' };
UPSERT message_category:confirmation CONTENT { name: 'Confirmation' };
UPSERT message_category:warning CONTENT { name: 'Warning' };
UPSERT message_category:debug CONTENT { name: 'Debug' };

-- ---------- message_type ----------
DEFINE TABLE IF NOT EXISTS message_type SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON message_type TYPE string;
DEFINE INDEX IF NOT EXISTS message_type_name_idx ON message_type FIELDS name UNIQUE;

UPSERT message_type:functional CONTENT { name: 'Functional' };
UPSERT message_type:technical CONTENT { name: 'Technical' };

-- ---------- message_state ----------
DEFINE TABLE IF NOT EXISTS message_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON message_state TYPE string;
DEFINE INDEX IF NOT EXISTS message_state_name_idx ON message_state FIELDS name UNIQUE;

UPSERT message_state:active CONTENT { name: 'Active' };
UPSERT message_state:inactive CONTENT { name: 'Inactive' };

-- ---------- message ----------
DEFINE TABLE IF NOT EXISTS message SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS code             ON message TYPE string;
DEFINE FIELD IF NOT EXISTS seed_key         ON message TYPE option<string>;
DEFINE FIELD IF NOT EXISTS title            ON message TYPE string;
DEFINE FIELD IF NOT EXISTS content          ON message TYPE string;
DEFINE FIELD IF NOT EXISTS type_id          ON message TYPE record<message_type> | string;
DEFINE FIELD IF NOT EXISTS category_id      ON message TYPE record<message_category> | string;
DEFINE FIELD IF NOT EXISTS status_id        ON message TYPE record<message_state> | string;
DEFINE FIELD IF NOT EXISTS application      ON message TYPE string;
DEFINE FIELD IF NOT EXISTS functionality_id ON message TYPE record<functionality> | string;
DEFINE INDEX IF NOT EXISTS message_code_idx ON message FIELDS code;
DEFINE INDEX IF NOT EXISTS message_catalogs_idx ON message FIELDS type_id, category_id, status_id;
DEFINE INDEX IF NOT EXISTS message_functionality_idx ON message FIELDS functionality_id;

-- ---------- message_environment_state ----------
DEFINE TABLE IF NOT EXISTS message_environment_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON message_environment_state TYPE string;
DEFINE INDEX IF NOT EXISTS message_environment_state_name_idx ON message_environment_state FIELDS name UNIQUE;

UPSERT message_environment_state:active CONTENT { name: 'Active' };
UPSERT message_environment_state:inactive CONTENT { name: 'Inactive' };

-- Alias used by existing Mongo collection constants.
DEFINE TABLE IF NOT EXISTS status_message_environment SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON status_message_environment TYPE string;
DEFINE INDEX IF NOT EXISTS status_message_environment_name_idx ON status_message_environment FIELDS name UNIQUE;

UPSERT status_message_environment:active CONTENT { name: 'Active' };
UPSERT status_message_environment:inactive CONTENT { name: 'Inactive' };

-- ---------- message_environment ----------
DEFINE TABLE IF NOT EXISTS message_environment SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS message_id     ON message_environment TYPE record<message> | string;
DEFINE FIELD IF NOT EXISTS seed_key       ON message_environment TYPE option<string>;
DEFINE FIELD IF NOT EXISTS environment_id ON message_environment TYPE record<environment> | string;
DEFINE FIELD IF NOT EXISTS state_data_id  ON message_environment TYPE record<message_environment_state> | string;
DEFINE INDEX IF NOT EXISTS message_environment_message_idx ON message_environment FIELDS message_id;
DEFINE INDEX IF NOT EXISTS message_environment_env_state_idx ON message_environment FIELDS environment_id, state_data_id;

-- ---------- message_environment_readmodel ----------
DEFINE TABLE OVERWRITE message_environment_readmodel SCHEMALESS;
DEFINE INDEX IF NOT EXISTS message_environment_readmodel_message_idx ON message_environment_readmodel FIELDS message_id;
DEFINE INDEX IF NOT EXISTS message_environment_readmodel_env_state_idx ON message_environment_readmodel FIELDS environment_id, state_data_id;

-- ---------- parameter ----------
DEFINE TABLE IF NOT EXISTS parameter SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS message_id  ON parameter TYPE record<message> | string;
DEFINE FIELD IF NOT EXISTS name        ON parameter TYPE string;
DEFINE FIELD IF NOT EXISTS description ON parameter TYPE string;
DEFINE INDEX IF NOT EXISTS parameter_message_idx ON parameter FIELDS message_id;

-- ---------- represent_parameter ----------
DEFINE TABLE IF NOT EXISTS represent_parameter SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS start             ON represent_parameter TYPE string;
DEFINE FIELD IF NOT EXISTS end_value         ON represent_parameter TYPE string;
DEFINE FIELD IF NOT EXISTS end               ON represent_parameter TYPE string;
DEFINE FIELD IF NOT EXISTS application_id    ON represent_parameter TYPE record<application> | string;
DEFINE FIELD IF NOT EXISTS default_parameter ON represent_parameter TYPE bool;
DEFINE FIELD IF NOT EXISTS parameter         ON represent_parameter TYPE bool;
DEFINE INDEX IF NOT EXISTS represent_parameter_application_idx ON represent_parameter FIELDS application_id;

-- ---------- token_state ----------
DEFINE TABLE IF NOT EXISTS token_state SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS name ON token_state TYPE string;
DEFINE INDEX IF NOT EXISTS token_state_name_idx ON token_state FIELDS name UNIQUE;

UPSERT token_state:active CONTENT { name: 'Active' };
UPSERT token_state:inactive CONTENT { name: 'Inactive' };

-- ---------- token ----------
DEFINE TABLE IF NOT EXISTS token SCHEMAFULL;
DEFINE FIELD IF NOT EXISTS secret_name     ON token TYPE string;
DEFINE FIELD IF NOT EXISTS creation_date   ON token TYPE datetime;
DEFINE FIELD IF NOT EXISTS expiration_date ON token TYPE datetime;
DEFINE FIELD IF NOT EXISTS environment_id  ON token TYPE record<environment> | string;
DEFINE FIELD IF NOT EXISTS state_id        ON token TYPE record<token_state> | string;
DEFINE INDEX IF NOT EXISTS token_env_state_idx ON token FIELDS environment_id, state_id;
