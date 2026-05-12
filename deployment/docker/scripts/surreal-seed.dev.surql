-- =============================================================
-- Development seed data for MessageUcoLab on SurrealDB
-- =============================================================
-- This file creates a disposable DEV fixture using generated UUIDs
-- and stores its generated identifiers in bootstrap_metadata.
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

DEFINE TABLE IF NOT EXISTS bootstrap_metadata SCHEMALESS;
DEFINE INDEX IF NOT EXISTS bootstrap_metadata_name_idx ON bootstrap_metadata FIELDS name UNIQUE;

LET $seed_key = 'dev-bootstrap';
LET $run_id = rand::uuid();
LET $application_id = rand::uuid();
LET $environment_id = rand::uuid();
LET $functionality_id = rand::uuid();
LET $message_id = rand::uuid();
LET $message_environment_id = rand::uuid();

DELETE message_environment_readmodel WHERE seed_key = $seed_key;
DELETE message_environment WHERE seed_key = $seed_key;
DELETE message WHERE seed_key = $seed_key;
DELETE functionality WHERE seed_key = $seed_key;
DELETE environment WHERE seed_key = $seed_key;
DELETE application WHERE seed_key = $seed_key;

CREATE type::thing('application', $application_id) CONTENT {
  seed_key: $seed_key,
  name: 'MessageUcoLab DEV',
  language_id: language_base:es,
  start_date: time::now(),
  end_date: <datetime>'2026-12-31T23:59:59Z',
  state_id: application_state:active
};

CREATE type::thing('environment', $environment_id) CONTENT {
  seed_key: $seed_key,
  name: 'DEV',
  application_id: type::thing('application', $application_id),
  type_id: environment_type:develop,
  state_id: environment_state:active
};

CREATE type::thing('functionality', $functionality_id) CONTENT {
  seed_key: $seed_key,
  name: 'Testing messages',
  application_id: type::thing('application', $application_id),
  start_date: time::now(),
  end_date: <datetime>'2026-12-31T23:59:59Z',
  state_id: functionality_state:active
};

CREATE type::thing('message', $message_id) CONTENT {
  seed_key: $seed_key,
  code: 'MSG_TEST_001',
  title: 'Mensaje test',
  content: 'Mensaje de prueba desde Surreal read model',
  type_id: message_type:functional,
  category_id: message_category:error,
  status_id: message_state:active,
  application: 'MessageUcoLab',
  functionality_id: type::thing('functionality', $functionality_id)
};

CREATE type::thing('message_environment', $message_environment_id) CONTENT {
  seed_key: $seed_key,
  message_id: type::thing('message', $message_id),
  environment_id: type::thing('environment', $environment_id),
  state_data_id: message_environment_state:active
};

CREATE type::thing('message_environment_readmodel', $message_environment_id) CONTENT {
  seed_key: $seed_key,
  message_id: type::thing('message', $message_id),
  environment_id: type::thing('environment', $environment_id),
  state_data_id: message_environment_state:active,
  message: {
    id: $message_id,
    application: 'MessageUcoLab',
    category: { id: message_category:error, name: 'Error' },
    code: 'MSG_TEST_001',
    content: 'Mensaje de prueba desde Surreal read model',
    functionality: { id: $functionality_id, name: 'Testing messages' },
    status: { id: message_state:active, name: 'Active' },
    title: 'Mensaje test',
    type: { id: message_type:functional, name: 'Functional' }
  },
  status: { id: message_environment_state:active, name: 'Active' }
};

UPSERT bootstrap_metadata:latest_dev_seed CONTENT {
  name: 'latest_dev_seed',
  seed_key: $seed_key,
  run_id: $run_id,
  created_at: time::now(),
  application_id: type::thing('application', $application_id),
  environment_id: type::thing('environment', $environment_id),
  functionality_id: type::thing('functionality', $functionality_id),
  message_id: type::thing('message', $message_id),
  message_environment_id: type::thing('message_environment', $message_environment_id),
  message_code: 'MSG_TEST_001'
};
