-- =============================================================
-- MESSAGE UCOLAB - CQRS + EVENT SOURCING + CHANGEFEED
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

-- =============================================================
-- DOMAIN EVENTS
-- =============================================================

DEFINE TABLE IF NOT EXISTS domain_events SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS event_type     ON domain_events TYPE string;
DEFINE FIELD IF NOT EXISTS aggregate_id   ON domain_events TYPE string;
DEFINE FIELD IF NOT EXISTS aggregate_type ON domain_events TYPE string;
DEFINE FIELD IF NOT EXISTS version        ON domain_events TYPE int;
DEFINE FIELD IF NOT EXISTS payload        ON domain_events TYPE object;
DEFINE FIELD IF NOT EXISTS metadata       ON domain_events TYPE object;
DEFINE FIELD IF NOT EXISTS created_at     ON domain_events TYPE datetime VALUE time::now();
DEFINE FIELD IF NOT EXISTS created_by     ON domain_events TYPE option<string>;
DEFINE FIELD IF NOT EXISTS correlation_id ON domain_events TYPE option<string>;
DEFINE FIELD IF NOT EXISTS causation_id   ON domain_events TYPE option<string>;

DEFINE INDEX IF NOT EXISTS domain_events_aggregate_idx
ON domain_events FIELDS aggregate_id, aggregate_type;

DEFINE INDEX IF NOT EXISTS domain_events_event_type_idx
ON domain_events FIELDS event_type;

DEFINE INDEX IF NOT EXISTS domain_events_created_at_idx
ON domain_events FIELDS created_at;

-- =============================================================
-- CATALOGS
-- =============================================================

DEFINE TABLE IF NOT EXISTS language_base SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS language ON language_base TYPE string;
DEFINE FIELD IF NOT EXISTS code     ON language_base TYPE string;

DEFINE INDEX IF NOT EXISTS language_base_code_idx
ON language_base FIELDS code UNIQUE;

UPSERT language_base:en CONTENT {
    language: "English",
    code: "EN"
};

UPSERT language_base:es CONTENT {
    language: "Spanish",
    code: "ES"
};

UPSERT language_base:fr CONTENT {
    language: "French",
    code: "FR"
};

-- =============================================================
-- APPLICATION STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS application_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON application_state TYPE string;

DEFINE INDEX IF NOT EXISTS application_state_name_idx
ON application_state FIELDS name UNIQUE;

UPSERT application_state:active CONTENT {
    name: "Active"
};

UPSERT application_state:inactive CONTENT {
    name: "Inactive"
};

-- =============================================================
-- APPLICATION
-- =============================================================

DEFINE TABLE IF NOT EXISTS application SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS name
ON application TYPE string;

DEFINE FIELD IF NOT EXISTS language_id
ON application TYPE record<language_base>;

DEFINE FIELD IF NOT EXISTS start_date
ON application TYPE datetime;

DEFINE FIELD IF NOT EXISTS end_date
ON application TYPE datetime;

DEFINE FIELD IF NOT EXISTS state_id
ON application TYPE record<application_state>;

DEFINE FIELD IF NOT EXISTS version
ON application TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON application TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON application TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS application_name_idx
ON application FIELDS name;

DEFINE INDEX IF NOT EXISTS application_language_state_idx
ON application FIELDS language_id, state_id;

-- =============================================================
-- APPLICATION EVENTS
-- =============================================================

DEFINE EVENT application_created
ON application
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "APPLICATION_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "application",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT application_updated
ON application
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "APPLICATION_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "application",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    };

    UPDATE $after.id SET
        version += 1,
        updated_at = time::now()
);

DEFINE EVENT application_deleted
ON application
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "APPLICATION_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "application",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- ENVIRONMENT TYPE
-- =============================================================

DEFINE TABLE IF NOT EXISTS environment_type SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON environment_type TYPE string;

DEFINE INDEX IF NOT EXISTS environment_type_name_idx
ON environment_type FIELDS name UNIQUE;

UPSERT environment_type:develop CONTENT {
    name: "Develop"
};

UPSERT environment_type:production CONTENT {
    name: "Production"
};

UPSERT environment_type:testing CONTENT {
    name: "Testing"
};

-- =============================================================
-- ENVIRONMENT STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS environment_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON environment_state TYPE string;

DEFINE INDEX IF NOT EXISTS environment_state_name_idx
ON environment_state FIELDS name UNIQUE;

UPSERT environment_state:active CONTENT {
    name: "Active"
};

UPSERT environment_state:inactive CONTENT {
    name: "Inactive"
};

-- =============================================================
-- ENVIRONMENT
-- =============================================================

DEFINE TABLE IF NOT EXISTS environment SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS name
ON environment TYPE string;

DEFINE FIELD IF NOT EXISTS application_id
ON environment TYPE record<application>;

DEFINE FIELD IF NOT EXISTS type_id
ON environment TYPE record<environment_type>;

DEFINE FIELD IF NOT EXISTS state_id
ON environment TYPE record<environment_state>;

DEFINE FIELD IF NOT EXISTS version
ON environment TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON environment TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON environment TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS environment_application_idx
ON environment FIELDS application_id;

DEFINE INDEX IF NOT EXISTS environment_type_state_idx
ON environment FIELDS type_id, state_id;

-- =============================================================
-- ENVIRONMENT EVENTS
-- =============================================================

DEFINE EVENT environment_created
ON environment
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "ENVIRONMENT_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "environment",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- MESSAGE STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON message_state TYPE string;

DEFINE INDEX IF NOT EXISTS message_state_name_idx
ON message_state FIELDS name UNIQUE;

UPSERT message_state:active CONTENT {
    name: "Active"
};

UPSERT message_state:inactive CONTENT {
    name: "Inactive"
};

-- =============================================================
-- MESSAGE TYPE
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_type SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON message_type TYPE string;

DEFINE INDEX IF NOT EXISTS message_type_name_idx
ON message_type FIELDS name UNIQUE;

UPSERT message_type:functional CONTENT {
    name: "Functional"
};

UPSERT message_type:technical CONTENT {
    name: "Technical"
};

-- =============================================================
-- MESSAGE CATEGORY
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_category SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON message_category TYPE string;

DEFINE INDEX IF NOT EXISTS message_category_name_idx
ON message_category FIELDS name UNIQUE;

UPSERT message_category:error CONTENT {
    name: "Error"
};

UPSERT message_category:information CONTENT {
    name: "Information"
};

UPSERT message_category:confirmation CONTENT {
    name: "Confirmation"
};

UPSERT message_category:warning CONTENT {
    name: "Warning"
};

UPSERT message_category:debug CONTENT {
    name: "Debug"
};

-- =============================================================
-- MESSAGE
-- =============================================================

DEFINE TABLE IF NOT EXISTS message SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS code
ON message TYPE string;

DEFINE FIELD IF NOT EXISTS title
ON message TYPE string;

DEFINE FIELD IF NOT EXISTS content
ON message TYPE string;

DEFINE FIELD IF NOT EXISTS type_id
ON message TYPE record<message_type>;

DEFINE FIELD IF NOT EXISTS category_id
ON message TYPE record<message_category>;

DEFINE FIELD IF NOT EXISTS status_id
ON message TYPE record<message_state>;

DEFINE FIELD IF NOT EXISTS application_id
ON message TYPE record<application>;

DEFINE FIELD IF NOT EXISTS version
ON message TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON message TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON message TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS message_code_idx
ON message FIELDS code;

DEFINE INDEX IF NOT EXISTS message_catalogs_idx
ON message FIELDS type_id, category_id, status_id;

-- =============================================================
-- MESSAGE EVENTS
-- =============================================================

DEFINE EVENT message_created
ON message
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "message",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT message_updated
ON message
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "message",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    };

    UPDATE $after.id SET
        version += 1,
        updated_at = time::now()
);

-- =============================================================
-- DOCUMENTAL PROJECTIONS
-- =============================================================

DEFINE TABLE IF NOT EXISTS application_document SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON application_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON application_document TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON application_document TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS application_document_id_idx
ON application_document FIELDS application_id;

DEFINE INDEX IF NOT EXISTS application_document_state_name_idx
ON application_document FIELDS state.name;

-- =============================================================
-- MESSAGE DOCUMENT COLLECTION
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_data_collection SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON message_data_collection TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON message_data_collection TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON message_data_collection TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS message_data_collection_message_id_idx
ON message_data_collection FIELDS message_id;

DEFINE INDEX IF NOT EXISTS message_data_collection_status_name_idx
ON message_data_collection FIELDS status.name;