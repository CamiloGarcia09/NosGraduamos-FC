OPTION IMPORT;

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
DEFINE FIELD OVERWRITE payload            ON domain_events TYPE object FLEXIBLE;
DEFINE FIELD OVERWRITE metadata           ON domain_events TYPE object FLEXIBLE;
DEFINE FIELD IF NOT EXISTS created_at     ON domain_events TYPE datetime VALUE time::now();
DEFINE FIELD IF NOT EXISTS created_by     ON domain_events TYPE option<string>;
DEFINE FIELD IF NOT EXISTS correlation_id ON domain_events TYPE option<string>;
DEFINE FIELD IF NOT EXISTS causation_id   ON domain_events TYPE option<string>;
DEFINE FIELD IF NOT EXISTS projection_status ON domain_events TYPE option<string> DEFAULT "pending";
DEFINE FIELD IF NOT EXISTS projected_at      ON domain_events TYPE option<datetime>;
DEFINE FIELD IF NOT EXISTS projection_error  ON domain_events TYPE option<string>;

DEFINE INDEX IF NOT EXISTS domain_events_aggregate_idx
ON domain_events FIELDS aggregate_id, aggregate_type;

DEFINE INDEX IF NOT EXISTS domain_events_event_type_idx
ON domain_events FIELDS event_type;

DEFINE INDEX IF NOT EXISTS domain_events_created_at_idx
ON domain_events FIELDS created_at;

DEFINE INDEX IF NOT EXISTS domain_events_projection_idx
ON domain_events FIELDS projection_status, created_at;

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
-- ORGANIZATION
-- =============================================================

DEFINE TABLE IF NOT EXISTS organization SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS name
ON organization TYPE string;

DEFINE FIELD IF NOT EXISTS version
ON organization TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON organization TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON organization TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS organization_name_idx
ON organization FIELDS name UNIQUE;

-- =============================================================
-- EXTERNAL IDENTITY
-- =============================================================

DEFINE TABLE IF NOT EXISTS external_identity SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS issuer
ON external_identity TYPE string;

DEFINE FIELD IF NOT EXISTS subject
ON external_identity TYPE string;

DEFINE FIELD IF NOT EXISTS email
ON external_identity TYPE option<string>;

DEFINE FIELD IF NOT EXISTS principal_type
ON external_identity TYPE string;

DEFINE FIELD IF NOT EXISTS created_at
ON external_identity TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON external_identity TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS external_identity_issuer_subject_idx
ON external_identity FIELDS issuer, subject UNIQUE;

-- =============================================================
-- APPLICATION
-- =============================================================

DEFINE TABLE IF NOT EXISTS application SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS seed_key
ON application TYPE option<string>;

DEFINE FIELD IF NOT EXISTS name
ON application TYPE string;

DEFINE FIELD IF NOT EXISTS organization_id
ON application TYPE record<organization>;

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

DEFINE INDEX IF NOT EXISTS application_organization_idx
ON application FIELDS organization_id;

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
    }
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

DEFINE FIELD IF NOT EXISTS seed_key
ON environment TYPE option<string>;

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
-- ACTIVE CONTEXT
-- =============================================================

DEFINE TABLE IF NOT EXISTS active_context SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS external_identity_id
ON active_context TYPE record<external_identity>;

DEFINE FIELD IF NOT EXISTS organization_id
ON active_context TYPE record<organization>;

DEFINE FIELD IF NOT EXISTS application_id
ON active_context TYPE record<application>;

DEFINE FIELD IF NOT EXISTS environment_id
ON active_context TYPE record<environment>;

DEFINE FIELD IF NOT EXISTS updated_at
ON active_context TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS active_context_external_identity_idx
ON active_context FIELDS external_identity_id UNIQUE;

DEFINE INDEX IF NOT EXISTS active_context_organization_idx
ON active_context FIELDS organization_id;

DEFINE INDEX IF NOT EXISTS active_context_application_idx
ON active_context FIELDS application_id;

DEFINE INDEX IF NOT EXISTS active_context_environment_idx
ON active_context FIELDS environment_id;

-- =============================================================
-- AUTHORIZATION
-- =============================================================

DEFINE TABLE IF NOT EXISTS membership SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS identity_id
ON membership TYPE record<external_identity>;

DEFINE FIELD IF NOT EXISTS organization_id
ON membership TYPE record<organization>;

DEFINE FIELD IF NOT EXISTS created_at
ON membership TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS membership_identity_organization_idx
ON membership FIELDS identity_id, organization_id UNIQUE;

DEFINE INDEX IF NOT EXISTS membership_organization_idx
ON membership FIELDS organization_id;

DEFINE TABLE IF NOT EXISTS permission SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS code
ON permission TYPE string;

DEFINE INDEX IF NOT EXISTS permission_code_idx
ON permission FIELDS code UNIQUE;

UPSERT permission:context_select CONTENT { code: "CONTEXT_SELECT" };
UPSERT permission:message_read CONTENT { code: "MESSAGE_READ" };
UPSERT permission:message_create CONTENT { code: "MESSAGE_CREATE" };
UPSERT permission:message_translate CONTENT { code: "MESSAGE_TRANSLATE" };
UPSERT permission:application_create CONTENT { code: "APPLICATION_CREATE" };
UPSERT permission:environment_create CONTENT { code: "ENVIRONMENT_CREATE" };
UPSERT permission:functionality_create CONTENT { code: "FUNCTIONALITY_CREATE" };

DEFINE TABLE IF NOT EXISTS role SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON role TYPE string;

DEFINE FIELD IF NOT EXISTS created_at
ON role TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS role_name_idx
ON role FIELDS name UNIQUE;

DEFINE TABLE IF NOT EXISTS role_permission SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS role_id
ON role_permission TYPE record<role>;

DEFINE FIELD IF NOT EXISTS permission_id
ON role_permission TYPE record<permission>;

DEFINE INDEX IF NOT EXISTS role_permission_role_permission_idx
ON role_permission FIELDS role_id, permission_id UNIQUE;

DEFINE INDEX IF NOT EXISTS role_permission_permission_idx
ON role_permission FIELDS permission_id;

DEFINE TABLE IF NOT EXISTS role_assignment SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS membership_id
ON role_assignment TYPE record<membership>;

DEFINE FIELD IF NOT EXISTS role_id
ON role_assignment TYPE record<role>;

DEFINE FIELD IF NOT EXISTS scope_type
ON role_assignment TYPE string ASSERT $value IN ["ORGANIZATION", "APPLICATION", "ENVIRONMENT"];

DEFINE FIELD IF NOT EXISTS organization_id
ON role_assignment TYPE option<record<organization>>;

DEFINE FIELD IF NOT EXISTS application_id
ON role_assignment TYPE option<record<application>>;

DEFINE FIELD IF NOT EXISTS environment_id
ON role_assignment TYPE option<record<environment>>;

DEFINE FIELD IF NOT EXISTS created_at
ON role_assignment TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS role_assignment_membership_idx
ON role_assignment FIELDS membership_id;

DEFINE INDEX IF NOT EXISTS role_assignment_role_idx
ON role_assignment FIELDS role_id;

DEFINE INDEX IF NOT EXISTS role_assignment_organization_scope_idx
ON role_assignment FIELDS organization_id;

DEFINE INDEX IF NOT EXISTS role_assignment_application_scope_idx
ON role_assignment FIELDS application_id;

DEFINE INDEX IF NOT EXISTS role_assignment_environment_scope_idx
ON role_assignment FIELDS environment_id;

DEFINE INDEX IF NOT EXISTS role_assignment_unique_idx
ON role_assignment FIELDS membership_id, role_id, scope_type,
    organization_id, application_id, environment_id UNIQUE;

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

DEFINE EVENT environment_updated
ON environment
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "ENVIRONMENT_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "environment",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT environment_deleted
ON environment
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "ENVIRONMENT_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "environment",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- FUNCTIONALITY STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS functionality_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON functionality_state TYPE string;

DEFINE INDEX IF NOT EXISTS functionality_state_name_idx
ON functionality_state FIELDS name UNIQUE;

UPSERT functionality_state:active CONTENT {
    name: "Active"
};

UPSERT functionality_state:inactive CONTENT {
    name: "Inactive"
};

-- =============================================================
-- FUNCTIONALITY
-- =============================================================

DEFINE TABLE IF NOT EXISTS functionality SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS seed_key
ON functionality TYPE option<string>;

DEFINE FIELD IF NOT EXISTS name
ON functionality TYPE string;

DEFINE FIELD IF NOT EXISTS application_id
ON functionality TYPE record<application>;

DEFINE FIELD IF NOT EXISTS start_date
ON functionality TYPE datetime;

DEFINE FIELD IF NOT EXISTS end_date
ON functionality TYPE datetime;

DEFINE FIELD IF NOT EXISTS state_id
ON functionality TYPE record<functionality_state>;

DEFINE FIELD IF NOT EXISTS version
ON functionality TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON functionality TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON functionality TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS functionality_application_idx
ON functionality FIELDS application_id;

DEFINE INDEX IF NOT EXISTS functionality_state_idx
ON functionality FIELDS state_id;

DEFINE EVENT functionality_created
ON functionality
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "FUNCTIONALITY_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "functionality",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT functionality_updated
ON functionality
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "FUNCTIONALITY_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "functionality",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT functionality_deleted
ON functionality
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "FUNCTIONALITY_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "functionality",
        version: $before.version,
        payload: $before,
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

DEFINE FIELD IF NOT EXISTS seed_key
ON message TYPE option<string>;

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
ON message TYPE option<record<application>>;

DEFINE FIELD IF NOT EXISTS application
ON message TYPE option<string>;

DEFINE FIELD IF NOT EXISTS functionality_id
ON message TYPE option<record<functionality>>;

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
    }
);

DEFINE EVENT message_deleted
ON message
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "message",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- MESSAGE ENVIRONMENT STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_environment_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON message_environment_state TYPE string;

DEFINE INDEX IF NOT EXISTS message_environment_state_name_idx
ON message_environment_state FIELDS name UNIQUE;

UPSERT message_environment_state:active CONTENT {
    name: "Active"
};

UPSERT message_environment_state:inactive CONTENT {
    name: "Inactive"
};

-- =============================================================
-- MESSAGE ENVIRONMENT
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_environment SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS seed_key
ON message_environment TYPE option<string>;

DEFINE FIELD IF NOT EXISTS message_id
ON message_environment TYPE record<message>;

DEFINE FIELD IF NOT EXISTS environment_id
ON message_environment TYPE record<environment>;

DEFINE FIELD IF NOT EXISTS state_data_id
ON message_environment TYPE record<message_environment_state>;

DEFINE FIELD IF NOT EXISTS version
ON message_environment TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON message_environment TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON message_environment TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS message_environment_environment_idx
ON message_environment FIELDS environment_id;

DEFINE INDEX IF NOT EXISTS message_environment_message_idx
ON message_environment FIELDS message_id;

DEFINE EVENT message_environment_created
ON message_environment
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_ENVIRONMENT_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "message_environment",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT message_environment_updated
ON message_environment
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_ENVIRONMENT_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "message_environment",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT message_environment_deleted
ON message_environment
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "MESSAGE_ENVIRONMENT_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "message_environment",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- PARAMETER
-- =============================================================

DEFINE TABLE IF NOT EXISTS parameter SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS message_id
ON parameter TYPE record<message>;

DEFINE FIELD IF NOT EXISTS name
ON parameter TYPE string;

DEFINE FIELD IF NOT EXISTS description
ON parameter TYPE string;

DEFINE FIELD IF NOT EXISTS version
ON parameter TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON parameter TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON parameter TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS parameter_message_idx
ON parameter FIELDS message_id;

DEFINE EVENT parameter_created
ON parameter
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "PARAMETER_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "parameter",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT parameter_updated
ON parameter
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "PARAMETER_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "parameter",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT parameter_deleted
ON parameter
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "PARAMETER_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "parameter",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- REPRESENT PARAMETER
-- =============================================================

DEFINE TABLE IF NOT EXISTS represent_parameter SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS start
ON represent_parameter TYPE string;

DEFINE FIELD IF NOT EXISTS end
ON represent_parameter TYPE string;

DEFINE FIELD IF NOT EXISTS application_id
ON represent_parameter TYPE record<application>;

DEFINE FIELD IF NOT EXISTS default_parameter
ON represent_parameter TYPE bool;

DEFINE FIELD IF NOT EXISTS parameter
ON represent_parameter TYPE bool;

DEFINE FIELD IF NOT EXISTS version
ON represent_parameter TYPE int VALUE 1;

DEFINE FIELD IF NOT EXISTS created_at
ON represent_parameter TYPE datetime VALUE time::now();

DEFINE FIELD IF NOT EXISTS updated_at
ON represent_parameter TYPE datetime VALUE time::now();

DEFINE INDEX IF NOT EXISTS represent_parameter_application_idx
ON represent_parameter FIELDS application_id;

DEFINE EVENT represent_parameter_created
ON represent_parameter
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "REPRESENT_PARAMETER_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "represent_parameter",
        version: $after.version,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT represent_parameter_updated
ON represent_parameter
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "REPRESENT_PARAMETER_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "represent_parameter",
        version: $after.version + 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT represent_parameter_deleted
ON represent_parameter
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "REPRESENT_PARAMETER_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "represent_parameter",
        version: $before.version,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- TOKEN STATE
-- =============================================================

DEFINE TABLE IF NOT EXISTS token_state SCHEMAFULL;

DEFINE FIELD IF NOT EXISTS name
ON token_state TYPE string;

DEFINE INDEX IF NOT EXISTS token_state_name_idx
ON token_state FIELDS name UNIQUE;

UPSERT token_state:`123e4567-e89b-12d3-a456-426614175000` CONTENT {
    name: "Active"
};

UPSERT token_state:`123e4567-e89b-12d3-a456-426614175001` CONTENT {
    name: "Inactive"
};

-- =============================================================
-- TOKEN
-- =============================================================

DEFINE TABLE IF NOT EXISTS token SCHEMAFULL CHANGEFEED 7d;

DEFINE FIELD IF NOT EXISTS secret_name
ON token TYPE string;

DEFINE FIELD IF NOT EXISTS creation_date
ON token TYPE datetime;

DEFINE FIELD IF NOT EXISTS expiration_date
ON token TYPE datetime;

DEFINE FIELD IF NOT EXISTS environment_id
ON token TYPE string;

DEFINE FIELD IF NOT EXISTS state_id
ON token TYPE string;

DEFINE INDEX IF NOT EXISTS token_environment_state_idx
ON token FIELDS environment_id, state_id;

DEFINE EVENT token_created
ON token
WHEN $event = "CREATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "TOKEN_CREATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "token",
        version: 1,
        payload: $after,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT token_updated
ON token
WHEN $event = "UPDATE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "TOKEN_UPDATED",
        aggregate_id: <string>$after.id,
        aggregate_type: "token",
        version: 1,
        payload: {
            before: $before,
            after: $after
        },
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

DEFINE EVENT token_deleted
ON token
WHEN $event = "DELETE"
THEN (
    CREATE domain_events CONTENT {
        event_type: "TOKEN_DELETED",
        aggregate_id: <string>$before.id,
        aggregate_type: "token",
        version: 1,
        payload: $before,
        metadata: {
            source: "surrealdb-event"
        },
        created_at: time::now()
    }
);

-- =============================================================
-- DOCUMENTAL PROJECTIONS
-- =============================================================

DEFINE TABLE IF NOT EXISTS domain_event_document SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON domain_event_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS aggregate_id
ON domain_event_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS aggregate_type
ON domain_event_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS event_type
ON domain_event_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS generated_at
ON domain_event_document TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS domain_event_document_source_idx
ON domain_event_document FIELDS source_id UNIQUE;

DEFINE INDEX IF NOT EXISTS domain_event_document_aggregate_idx
ON domain_event_document FIELDS aggregate_id, aggregate_type;

DEFINE TABLE IF NOT EXISTS application_document SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON application_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS application_id
ON application_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS organization_id
ON application_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON application_document TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON application_document TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS application_document_id_idx
ON application_document FIELDS application_id;

DEFINE INDEX IF NOT EXISTS application_document_organization_idx
ON application_document FIELDS organization_id;

DEFINE INDEX IF NOT EXISTS application_document_state_name_idx
ON application_document FIELDS state.name;

-- =============================================================
-- ENVIRONMENT DOCUMENT COLLECTION
-- =============================================================

DEFINE TABLE IF NOT EXISTS environment_document SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON environment_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS environment_id
ON environment_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS application_id
ON environment_document TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON environment_document TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON environment_document TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS environment_document_id_idx
ON environment_document FIELDS environment_id;

DEFINE INDEX IF NOT EXISTS environment_document_application_idx
ON environment_document FIELDS application_id;

DEFINE INDEX IF NOT EXISTS environment_document_state_name_idx
ON environment_document FIELDS state.name;

-- =============================================================
-- MESSAGE DOCUMENT COLLECTION
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_data_collection SCHEMALESS;

DEFINE FIELD IF NOT EXISTS source_id
ON message_data_collection TYPE option<string>;

DEFINE FIELD IF NOT EXISTS message_id
ON message_data_collection TYPE option<string>;

DEFINE FIELD IF NOT EXISTS environment_id
ON message_data_collection TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON message_data_collection TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON message_data_collection TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS message_data_collection_message_id_idx
ON message_data_collection FIELDS message_id;

DEFINE INDEX IF NOT EXISTS message_data_collection_status_name_idx
ON message_data_collection FIELDS status.name;

-- =============================================================
-- MESSAGE ENVIRONMENT READ MODEL
-- =============================================================

DEFINE TABLE IF NOT EXISTS message_environment_readmodel SCHEMALESS;

DEFINE FIELD IF NOT EXISTS seed_key
ON message_environment_readmodel TYPE option<string>;

DEFINE FIELD IF NOT EXISTS source_id
ON message_environment_readmodel TYPE option<string>;

DEFINE FIELD IF NOT EXISTS message_environment_id
ON message_environment_readmodel TYPE option<string>;

DEFINE FIELD IF NOT EXISTS message_id
ON message_environment_readmodel TYPE option<string>;

DEFINE FIELD IF NOT EXISTS environment_id
ON message_environment_readmodel TYPE option<string>;

DEFINE FIELD IF NOT EXISTS projection_version
ON message_environment_readmodel TYPE option<number>;

DEFINE FIELD IF NOT EXISTS generated_at
ON message_environment_readmodel TYPE option<datetime>;

DEFINE INDEX IF NOT EXISTS message_environment_readmodel_environment_idx
ON message_environment_readmodel FIELDS environment_id;

DEFINE INDEX IF NOT EXISTS message_environment_readmodel_message_code_idx
ON message_environment_readmodel FIELDS message.code;
