-- Tablas en KSQL

CREATE TABLE LANGUAGE_BASE_DATA (
                                    ID STRING PRIMARY KEY,
                                    LANGUAGE STRING,
                                    CODE STRING
) WITH (
      KAFKA_TOPIC='postgres.public.language_base_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE APPLICATION_STATE_DATA (
                                        ID STRING PRIMARY KEY,
                                        NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.application_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE ENVIRONMENT_STATE_DATA (
                                        ID STRING PRIMARY KEY,
                                        NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.environment_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE FUNCTIONALITY_STATE_DATA (
                                          ID STRING PRIMARY KEY,
                                          NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.functionality_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE MESSAGE_CATEGORY_DATA (
                                       ID STRING PRIMARY KEY,
                                       NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.message_category_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE MESSAGE_TYPE_DATA (
                                   ID STRING PRIMARY KEY,
                                   NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.message_type_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE MESSAGE_STATE_DATA (
                                    ID STRING PRIMARY KEY,
                                    NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.message_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE TOKEN_STATE_DATA (
                                  ID STRING PRIMARY KEY,
                                  NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.token_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE MESSAGE_ENVIRONMENT_STATE_DATA (
                                                ID STRING PRIMARY KEY,
                                                NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.message_environment_state_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE ENVIRONMENT_TYPE_DATA (
                                       ID STRING PRIMARY KEY,
                                       NAME STRING
) WITH (
      KAFKA_TOPIC='postgres.public.environment_type_data',
      KEY_FORMAT='KAFKA',
      VALUE_FORMAT='JSON'
      );

CREATE TABLE functionality_data_table (
                                          id STRING PRIMARY KEY,
                                          name STRING,
                                          application_id STRING,
                                          start_date TIMESTAMP,
                                          end_date TIMESTAMP,
                                          state_id STRING
) WITH (
      KAFKA_TOPIC='postgres.public.functionality_data',
      VALUE_FORMAT='JSON',
      KEY_FORMAT='KAFKA'
      );

CREATE TABLE message_data_table (
                                    id STRING PRIMARY KEY,
                                    code STRING,
                                    title STRING,
                                    content STRING,
                                    type_id STRING,
                                    category_id STRING,
                                    status_id STRING,
                                    application STRING,
                                    functionality_id STRING,
                                    __op STRING,
                                    __source_ts_ms BIGINT
) WITH (
      KAFKA_TOPIC='postgres.public.message_data',
      VALUE_FORMAT='JSON',
      KEY_FORMAT='KAFKA'
      );

-- Streams

CREATE STREAM application_data_stream (
    id STRING,
    name STRING,
    language_id STRING,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    state_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.application_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);


CREATE STREAM environment_data_stream (
    id STRING,
    name STRING,
    application_id STRING,
    type_id STRING,
    state_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.environment_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM functionality_data_stream (
    id STRING,
    name STRING,
    application_id STRING,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    state_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.functionality_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM message_data_stream (
    id STRING,
    code STRING,
    title STRING,
    content STRING,
    type_id STRING,
    category_id STRING,
    status_id STRING,
    application STRING,
    functionality_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.message_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);


CREATE STREAM message_environment_data_stream (
    id STRING,
    message_id STRING,
    environment_id STRING,
    state_data_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.message_environment_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM parameter_data_stream (
    id STRING,
    message_id STRING,
    name STRING,
    description STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.parameter_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM represent_parameter_data_stream (
    id STRING,
    start STRING,
    end_value STRING,
    application_id STRING,
    default_parameter BOOLEAN,
    parameter BOOLEAN,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.represent_parameter_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM token_data_stream (
    id STRING,
    secret_name STRING,
    creation_date TIMESTAMP,
    expiration_date TIMESTAMP,
    environment_id STRING,
    state_id STRING,
    __op STRING,
    __source_ts_ms BIGINT
) WITH (
    KAFKA_TOPIC='postgres.public.token_data',
    VALUE_FORMAT='JSON',
    KEY_FORMAT='KAFKA'
);

CREATE STREAM message_data_collection WITH (
    KAFKA_TOPIC='message_data_stream',
    PARTITIONS=1,
    REPLICAS=1
) AS
SELECT
    m.id AS `message_id`,
    m.code AS `code`,
    m.title AS `title`,
    m.content AS `content`,
    m.type_id AS `type_id`,
    m.category_id AS `category_id`,
    m.status_id AS `status_id`,
    -- Se anida la información del tipo
    CASE
        WHEN t.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= t.id, `name`:= t.name)
        END AS `type`,
    -- Se anida la información de la categoría
    CASE
        WHEN c.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= c.id, `name`:= c.name)
        END AS `category`,
    -- Se anida la información del estado
    CASE
        WHEN s.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= s.id, `name`:= s.name)
        END AS `status`,
    -- Se anida la información de la funcionalidad
    CASE
        WHEN f.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= f.id, `name`:= f.name)
        END AS `functionality`,
    m.application AS `application`,
    m.functionality_id AS `functionality_id`,
    m.__op AS `__op`,
    m.__source_ts_ms AS `__source_ts_ms`
FROM message_data_stream m
         LEFT JOIN message_type_data t ON m.type_id = t.id
         LEFT JOIN message_category_data c ON m.category_id = c.id
         LEFT JOIN message_state_data s ON m.status_id = s.id
         LEFT JOIN functionality_data_table f ON m.functionality_id = f.id
    EMIT CHANGES;

CREATE STREAM message_environment_data_stream_collection WITH (
    KAFKA_TOPIC='message_environment_data_stream',
    PARTITIONS=1,
    REPLICAS=1
) AS
SELECT
    m.id AS `message_environment_id`,
    m.message_id AS `message_id`,
    m.environment_id AS `environment_id`,
    m.state_data_id AS `state_data_id`,
    -- Se anida la información del mensaje
    CASE
        WHEN d.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= d.id,
                    `application`:= d.application,
                    `category`:= STRUCT(`id`:= c.id, `name`:= c.name),
                    `code`:= d.code,
                    `content`:= d.content,
                    `functionality`:= STRUCT(`id`:= f.id, `name`:= f.name),
                    `status`:= STRUCT(`id`:= s.id, `name`:= s.name),
                    `title`:= d.title,
                    `type`:= STRUCT(`id`:= t.id, `name`:= t.name)
             )
        END AS `message`,
    -- Se anida la información del estado del token
    CASE
        WHEN me.id IS NULL THEN NULL
        ELSE STRUCT(`id`:= me.id, `name`:= me.name)
        END AS `status`,
    m.__op AS `__op`,
    m.__source_ts_ms AS `__source_ts_ms`
FROM message_environment_data_stream m
         LEFT JOIN message_data_table d ON m.message_id = d.id
         LEFT JOIN message_type_data t ON d.type_id = t.id
         LEFT JOIN message_category_data c ON d.category_id = c.id
         LEFT JOIN message_state_data s ON d.status_id = s.id
         LEFT JOIN functionality_data_table f ON d.functionality_id = f.id
         LEFT JOIN message_environment_state_data me ON m.state_data_id = me.id
    EMIT CHANGES;