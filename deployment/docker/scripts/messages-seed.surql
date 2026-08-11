-- =============================================================
-- MESSAGE CATALOG SEED - System Messages
-- =============================================================
-- Loads all system messages (previously in DetailMessageEnum) into
-- SurrealDB as the source of truth for the message catalog.
-- Idempotent: uses UPSERT with deterministic record IDs.
-- =============================================================

USE NS messageucolab;
USE DB messageucolab;

-- Index to support findByCode() queries efficiently
DEFINE INDEX IF NOT EXISTS message_data_collection_code_idx
ON message_data_collection FIELDS code;

-- =============================================================
-- SYSTEM APPLICATION, FUNCTIONALITY, ENVIRONMENT
-- =============================================================

UPSERT application:catalog_system CONTENT {
    seed_key: 'messages-seed',
    name: 'MessageUcoLab System',
    language_id: language_base:en,
    start_date: <datetime>'2024-01-01T00:00:00Z',
    end_date: <datetime>'2099-12-31T23:59:59Z',
    state_id: application_state:active
};

UPSERT functionality:catalog_system CONTENT {
    seed_key: 'messages-seed',
    name: 'System Messages',
    application_id: application:catalog_system,
    start_date: <datetime>'2024-01-01T00:00:00Z',
    end_date: <datetime>'2099-12-31T23:59:59Z',
    state_id: functionality_state:active
};

UPSERT environment:catalog_system CONTENT {
    seed_key: 'messages-seed',
    name: 'SYSTEM',
    application_id: application:catalog_system,
    type_id: environment_type:production,
    state_id: environment_state:active
};

-- =============================================================
-- HELPER: upsert a message + its data_collection projection
-- Each message uses its code as the record ID for determinism.
-- =============================================================

-- TCH_001
UPSERT message:TCH_001 CONTENT { seed_key: 'messages-seed', code: 'TCH_001', title: 'Database not connected', content: 'Unable to establish connection to %s database', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_001 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_001', code: 'TCH_001', title: 'Database not connected', content: 'Unable to establish connection to %s database', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_002
UPSERT message:TCH_002 CONTENT { seed_key: 'messages-seed', code: 'TCH_002', title: 'Message Broker not connected', content: 'Unable to establish connection to Message Broker', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_002 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_002', code: 'TCH_002', title: 'Message Broker not connected', content: 'Unable to establish connection to Message Broker', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_003
UPSERT message:TCH_003 CONTENT { seed_key: 'messages-seed', code: 'TCH_003', title: 'API Translate not connected', content: 'Unable to establish connection to translate API', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_003 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_003', code: 'TCH_003', title: 'API Translate not connected', content: 'Unable to establish connection to translate API', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_004
UPSERT message:TCH_004 CONTENT { seed_key: 'messages-seed', code: 'TCH_004', title: 'Cache not connected', content: 'Unable to establish connection to cache', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_004 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_004', code: 'TCH_004', title: 'Cache not connected', content: 'Unable to establish connection to cache', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_005
UPSERT message:TCH_005 CONTENT { seed_key: 'messages-seed', code: 'TCH_005', title: 'Parameters component not connected', content: 'Unable to establish connection to Parameters component', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_005 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_005', code: 'TCH_005', title: 'Parameters component not connected', content: 'Unable to establish connection to Parameters component', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_006
UPSERT message:TCH_006 CONTENT { seed_key: 'messages-seed', code: 'TCH_006', title: 'Security component not connected', content: 'Unable to establish connection to Security component', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_006 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_006', code: 'TCH_006', title: 'Security component not connected', content: 'Unable to establish connection to Security component', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_007
UPSERT message:TCH_007 CONTENT { seed_key: 'messages-seed', code: 'TCH_007', title: 'Key is null', content: 'The message key is null', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_007 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_007', code: 'TCH_007', title: 'Key is null', content: 'The message key is null', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_008
UPSERT message:TCH_008 CONTENT { seed_key: 'messages-seed', code: 'TCH_008', title: 'Key is empty', content: 'Message code does not exist', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_008 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_008', code: 'TCH_008', title: 'Key is empty', content: 'Message code does not exist', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_009
UPSERT message:TCH_009 CONTENT { seed_key: 'messages-seed', code: 'TCH_009', title: 'Message not found', content: 'Message code does not exist with %s key', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_009 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_009', code: 'TCH_009', title: 'Message not found', content: 'Message code does not exist with %s key', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_010
UPSERT message:TCH_010 CONTENT { seed_key: 'messages-seed', code: 'TCH_010', title: 'Code is required', content: 'Code is required', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_010 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_010', code: 'TCH_010', title: 'Code is required', content: 'Code is required', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_011
UPSERT message:TCH_011 CONTENT { seed_key: 'messages-seed', code: 'TCH_011', title: 'Content is required', content: 'Content is required', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_011 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_011', code: 'TCH_011', title: 'Content is required', content: 'Content is required', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_012
UPSERT message:TCH_012 CONTENT { seed_key: 'messages-seed', code: 'TCH_012', title: 'Title is required', content: 'Title is required', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_012 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_012', code: 'TCH_012', title: 'Title is required', content: 'Title is required', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_013
UPSERT message:TCH_013 CONTENT { seed_key: 'messages-seed', code: 'TCH_013', title: 'Type is required', content: 'Type is required', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_013 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_013', code: 'TCH_013', title: 'Type is required', content: 'Type is required', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_014
UPSERT message:TCH_014 CONTENT { seed_key: 'messages-seed', code: 'TCH_014', title: 'Category is required', content: 'Category is required', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_014 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_014', code: 'TCH_014', title: 'Category is required', content: 'Category is required', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_015
UPSERT message:TCH_015 CONTENT { seed_key: 'messages-seed', code: 'TCH_015', title: 'Message found in application', content: 'Message with code {} in application {} found {}', type_id: message_type:technical, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_015 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_015', code: 'TCH_015', title: 'Message found in application', content: 'Message with code {} in application {} found {}', type: { name: 'Technical' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_016
UPSERT message:TCH_016 CONTENT { seed_key: 'messages-seed', code: 'TCH_016', title: 'Validation Error', content: 'Validation error with correlation id {}', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_016 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_016', code: 'TCH_016', title: 'Validation Error', content: 'Validation error with correlation id {}', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_017
UPSERT message:TCH_017 CONTENT { seed_key: 'messages-seed', code: 'TCH_017', title: 'Serializer not found', content: "No serializer was found for media type '{}' and there is no default serializer configured.", type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_017 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_017', code: 'TCH_017', title: 'Serializer not found', content: "No serializer was found for media type '{}' and there is no default serializer configured.", type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_018
UPSERT message:TCH_018 CONTENT { seed_key: 'messages-seed', code: 'TCH_018', title: 'Serializer error', content: 'Error when serializing the object', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_018 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_018', code: 'TCH_018', title: 'Serializer error', content: 'Error when serializing the object', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_019
UPSERT message:TCH_019 CONTENT { seed_key: 'messages-seed', code: 'TCH_019', title: 'Error when serializing error response', content: 'An error occurred while trying to serialize the error response.', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_019 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_019', code: 'TCH_019', title: 'Error when serializing error response', content: 'An error occurred while trying to serialize the error response.', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_020
UPSERT message:TCH_020 CONTENT { seed_key: 'messages-seed', code: 'TCH_020', title: 'Response Error', content: 'The error response is: {}', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_020 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_020', code: 'TCH_020', title: 'Response Error', content: 'The error response is: {}', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_021
UPSERT message:TCH_021 CONTENT { seed_key: 'messages-seed', code: 'TCH_021', title: 'Response success', content: 'The successful response is: {}', type_id: message_type:technical, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_021 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_021', code: 'TCH_021', title: 'Response success', content: 'The successful response is: {}', type: { name: 'Technical' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_022
UPSERT message:TCH_022 CONTENT { seed_key: 'messages-seed', code: 'TCH_022', title: 'Media type not supported', content: 'The media type %s is not supported by the system', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_022 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_022', code: 'TCH_022', title: 'Media type not supported', content: 'The media type %s is not supported by the system', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_023
UPSERT message:TCH_023 CONTENT { seed_key: 'messages-seed', code: 'TCH_023', title: 'Incorrect Media type', content: 'Media type is not supported {}', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_023 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_023', code: 'TCH_023', title: 'Incorrect Media type', content: 'Media type is not supported {}', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_024
UPSERT message:TCH_024 CONTENT { seed_key: 'messages-seed', code: 'TCH_024', title: 'KeyPair generation failed', content: 'An error occurred while generating the key pair', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_024 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_024', code: 'TCH_024', title: 'KeyPair generation failed', content: 'An error occurred while generating the key pair', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_025
UPSERT message:TCH_025 CONTENT { seed_key: 'messages-seed', code: 'TCH_025', title: 'Error generating token', content: 'An error occurred while trying to create the token', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_025 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_025', code: 'TCH_025', title: 'Error generating token', content: 'An error occurred while trying to create the token', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_026
UPSERT message:TCH_026 CONTENT { seed_key: 'messages-seed', code: 'TCH_026', title: 'Error generating keys', content: 'An error occurred while trying to create the token', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_026 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_026', code: 'TCH_026', title: 'Error generating keys', content: 'An error occurred while trying to create the token', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_027
UPSERT message:TCH_027 CONTENT { seed_key: 'messages-seed', code: 'TCH_027', title: 'Error generating signature', content: 'An error occurred while trying to generating the signature', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_027 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_027', code: 'TCH_027', title: 'Error generating signature', content: 'An error occurred while trying to generating the signature', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_028
UPSERT message:TCH_028 CONTENT { seed_key: 'messages-seed', code: 'TCH_028', title: 'Error verifying access', content: 'An error occurred while trying to verify access with the private key {} , the signature {} and the secret {} provided', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_028 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_028', code: 'TCH_028', title: 'Error verifying access', content: 'An error occurred while trying to verify access with the private key {} , the signature {} and the secret {} provided', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_029
UPSERT message:TCH_029 CONTENT { seed_key: 'messages-seed', code: 'TCH_029', title: 'Error request to Doppler', content: 'An error occurred sending request to Doppler', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_029 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_029', code: 'TCH_029', title: 'Error request to Doppler', content: 'An error occurred sending request to Doppler', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_030
UPSERT message:TCH_030 CONTENT { seed_key: 'messages-seed', code: 'TCH_030', title: 'Error response code', content: 'The error code of the response is %s: ', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_030 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_030', code: 'TCH_030', title: 'Error response code', content: 'The error code of the response is %s: ', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_031
UPSERT message:TCH_031 CONTENT { seed_key: 'messages-seed', code: 'TCH_031', title: 'Access denied', content: 'Access denied, the token is invalid', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_031 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_031', code: 'TCH_031', title: 'Access denied', content: 'zAccess denied, the token is invalid', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_032
UPSERT message:TCH_032 CONTENT { seed_key: 'messages-seed', code: 'TCH_032', title: 'Token header not sent', content: "Access denied, the header 'Token' has not been send", type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_032 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_032', code: 'TCH_032', title: 'Token header not sent', content: "Access denied, the header 'Token' has not been send", type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_033
UPSERT message:TCH_033 CONTENT { seed_key: 'messages-seed', code: 'TCH_033', title: 'Token expired', content: 'Access denied, the token is expired or inactive', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_033 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_033', code: 'TCH_033', title: 'Token expired', content: 'Access denied, the token is expired or inactive', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_034
UPSERT message:TCH_034 CONTENT { seed_key: 'messages-seed', code: 'TCH_034', title: 'SHA-256 not available', content: 'SHA-256 algorithm not available', type_id: message_type:technical, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_034 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_034', code: 'TCH_034', title: 'SHA-256 not available', content: 'SHA-256 algorithm not available', type: { name: 'Technical' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- TCH_035
UPSERT message:TCH_035 CONTENT { seed_key: 'messages-seed', code: 'TCH_035', title: 'Environment id does not exist', content: 'The environment with the id %s provided does not exist.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:TCH_035 CONTENT { seed_key: 'messages-seed', message_id: 'message:TCH_035', code: 'TCH_035', title: 'Environment id does not exist', content: 'The environment with the id %s provided does not exist.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_001
UPSERT message:FUN_001 CONTENT { seed_key: 'messages-seed', code: 'FUN_001', title: 'Invalid message code', content: 'The message code is invalid or not within allowed values', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_001 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_001', code: 'FUN_001', title: 'Invalid message code', content: 'The message code is invalid or not within allowed values', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_002
UPSERT message:FUN_002 CONTENT { seed_key: 'messages-seed', code: 'FUN_002', title: 'Title is empty or null', content: 'The message must have a valid title', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_002 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_002', code: 'FUN_002', title: 'Title is empty or null', content: 'The message must have a valid title', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_003
UPSERT message:FUN_003 CONTENT { seed_key: 'messages-seed', code: 'FUN_003', title: 'Content is empty or null', content: 'The message must include a valid description', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_003 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_003', code: 'FUN_003', title: 'Content is empty or null', content: 'The message must include a valid description', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_004
UPSERT message:FUN_004 CONTENT { seed_key: 'messages-seed', code: 'FUN_004', title: 'Message type is null', content: 'The message type is not defined', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_004 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_004', code: 'FUN_004', title: 'Message type is null', content: 'The message type is not defined', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_005
UPSERT message:FUN_005 CONTENT { seed_key: 'messages-seed', code: 'FUN_005', title: 'Message category is null', content: 'The message category is not defined', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_005 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_005', code: 'FUN_005', title: 'Message category is null', content: 'The message category is not defined', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_006
UPSERT message:FUN_006 CONTENT { seed_key: 'messages-seed', code: 'FUN_006', title: 'Message not found in cache', content: 'The message was not found in cache, we proceed to search in database', type_id: message_type:functional, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_006 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_006', code: 'FUN_006', title: 'Message not found in cache', content: 'The message was not found in cache, we proceed to search in database', type: { name: 'Functional' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_007
UPSERT message:FUN_007 CONTENT { seed_key: 'messages-seed', code: 'FUN_007', title: 'Message found in database', content: 'Messages were found in the database, proceed to return and cache.', type_id: message_type:functional, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_007 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_007', code: 'FUN_007', title: 'Message found in database', content: 'Messages were found in the database, proceed to return and cache.', type: { name: 'Functional' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_008
UPSERT message:FUN_008 CONTENT { seed_key: 'messages-seed', code: 'FUN_008', title: 'Cache and database messages mismatch', content: 'The number of messages in cache and database does not match, the cache is filled with the missing messages.', type_id: message_type:functional, category_id: message_category:warning, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_008 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_008', code: 'FUN_008', title: 'Cache and database messages mismatch', content: 'The number of messages in cache and database does not match, the cache is filled with the missing messages.', type: { name: 'Functional' }, category: { name: 'Warning' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_009
UPSERT message:FUN_009 CONTENT { seed_key: 'messages-seed', code: 'FUN_009', title: 'Message found in cache', content: 'Messages were found in cache, proceed to return.', type_id: message_type:functional, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_009 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_009', code: 'FUN_009', title: 'Message found in cache', content: 'Messages were found in cache, proceed to return.', type: { name: 'Functional' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_010
UPSERT message:FUN_010 CONTENT { seed_key: 'messages-seed', code: 'FUN_010', title: 'Null Validator', content: '%s is required', type_id: message_type:functional, category_id: message_category:information, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_010 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_010', code: 'FUN_010', title: 'Null Validator', content: '%s is required', type: { name: 'Functional' }, category: { name: 'Information' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_011
UPSERT message:FUN_011 CONTENT { seed_key: 'messages-seed', code: 'FUN_011', title: 'Application not found', content: 'Could not get the messages from the application, verify that the application exists.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_011 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_011', code: 'FUN_011', title: 'Application not found', content: 'Could not get the messages from the application, verify that the application exists.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_012
UPSERT message:FUN_012 CONTENT { seed_key: 'messages-seed', code: 'FUN_012', title: 'Message not found', content: 'There is no message with the code %s for the application %s', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_012 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_012', code: 'FUN_012', title: 'Message not found', content: 'There is no message with the code %s for the application %s', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_013
UPSERT message:FUN_013 CONTENT { seed_key: 'messages-seed', code: 'FUN_013', title: 'Cache not connected', content: 'Failed to connect to Redis', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_013 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_013', code: 'FUN_013', title: 'Cache not connected', content: 'Failed to connect to Redis', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_014
UPSERT message:FUN_014 CONTENT { seed_key: 'messages-seed', code: 'FUN_014', title: 'Error during cache connection', content: 'Data access exception while connecting to Redis', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_014 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_014', code: 'FUN_014', title: 'Error during cache connection', content: 'Data access exception while connecting to Redis', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_015
UPSERT message:FUN_015 CONTENT { seed_key: 'messages-seed', code: 'FUN_015', title: 'Unexpected exception', content: 'Unexpected exception while connecting to Redis', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_015 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_015', code: 'FUN_015', title: 'Unexpected exception', content: 'Unexpected exception while connecting to Redis', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_017
UPSERT message:FUN_017 CONTENT { seed_key: 'messages-seed', code: 'FUN_017', title: 'Content cannot be empty', content: 'The content cannot be empty', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_017 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_017', code: 'FUN_017', title: 'Content cannot be empty', content: 'The content cannot be empty', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_018
UPSERT message:FUN_018 CONTENT { seed_key: 'messages-seed', code: 'FUN_018', title: 'Content size below minimum', content: 'The size of the content must not be less than 10', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_018 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_018', code: 'FUN_018', title: 'Content size below minimum', content: 'The size of the content must not be less than 10', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_019
UPSERT message:FUN_019 CONTENT { seed_key: 'messages-seed', code: 'FUN_019', title: 'Content size exceeds limit', content: 'The size of the content cannot be larger than 100', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_019 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_019', code: 'FUN_019', title: 'Content size exceeds limit', content: 'The size of the content cannot be larger than 100', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_020
UPSERT message:FUN_020 CONTENT { seed_key: 'messages-seed', code: 'FUN_020', title: 'Title size below minimum', content: 'The size of the title cannot be smaller than 10', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_020 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_020', code: 'FUN_020', title: 'Title size below minimum', content: 'The size of the title cannot be smaller than 10', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_021
UPSERT message:FUN_021 CONTENT { seed_key: 'messages-seed', code: 'FUN_021', title: 'Title size exceeds limit', content: 'The size of the title cannot be larger than 50', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_021 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_021', code: 'FUN_021', title: 'Title size exceeds limit', content: 'The size of the title cannot be larger than 50', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_022
UPSERT message:FUN_022 CONTENT { seed_key: 'messages-seed', code: 'FUN_022', title: 'Title cannot be empty', content: 'The title cannot be empty', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_022 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_022', code: 'FUN_022', title: 'Title cannot be empty', content: 'The title cannot be empty', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_023
UPSERT message:FUN_023 CONTENT { seed_key: 'messages-seed', code: 'FUN_023', title: 'Unexpected Error', content: 'An unexpected error has occurred', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_023 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_023', code: 'FUN_023', title: 'Unexpected Error', content: 'An unexpected error has occurred', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_024
UPSERT message:FUN_024 CONTENT { seed_key: 'messages-seed', code: 'FUN_024', title: 'Information consulted', content: '%s', type_id: message_type:functional, category_id: message_category:confirmation, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_024 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_024', code: 'FUN_024', title: 'Information consulted', content: '%s', type: { name: 'Functional' }, category: { name: 'Confirmation' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_025
UPSERT message:FUN_025 CONTENT { seed_key: 'messages-seed', code: 'FUN_025', title: 'Error create token', content: 'An error occurred while verify the token, please try again later', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_025 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_025', code: 'FUN_025', title: 'Error create token', content: 'An error occurred while verify the token, please try again later', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_026
UPSERT message:FUN_026 CONTENT { seed_key: 'messages-seed', code: 'FUN_026', title: 'Token not found', content: 'The token is not found', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_026 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_026', code: 'FUN_026', title: 'Token not found', content: 'The token is not found', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_027
UPSERT message:FUN_027 CONTENT { seed_key: 'messages-seed', code: 'FUN_027', title: 'Information consulted', content: '%s', type_id: message_type:functional, category_id: message_category:confirmation, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_027 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_027', code: 'FUN_027', title: 'Information consulted', content: '%s', type: { name: 'Functional' }, category: { name: 'Confirmation' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_028
UPSERT message:FUN_028 CONTENT { seed_key: 'messages-seed', code: 'FUN_028', title: 'Invalid page number', content: 'The page number must be between 1 and %d.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_028 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_028', code: 'FUN_028', title: 'Invalid page number', content: 'The page number must be between 1 and %d.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_029
UPSERT message:FUN_029 CONTENT { seed_key: 'messages-seed', code: 'FUN_029', title: 'Invalid page size', content: 'The page size must be between 1 and %d.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_029 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_029', code: 'FUN_029', title: 'Invalid page size', content: 'The page size must be between 1 and %d.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_030
UPSERT message:FUN_030 CONTENT { seed_key: 'messages-seed', code: 'FUN_030', title: 'Invalid sort column', content: "The sort column '%s' is not valid.", type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_030 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_030', code: 'FUN_030', title: 'Invalid sort column', content: "The sort column '%s' is not valid.", type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_031
UPSERT message:FUN_031 CONTENT { seed_key: 'messages-seed', code: 'FUN_031', title: 'Invalid sort direction', content: "The sort direction must be 'ASC' or 'DESC'.", type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_031 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_031', code: 'FUN_031', title: 'Invalid sort direction', content: "The sort direction must be 'ASC' or 'DESC'.", type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_032
UPSERT message:FUN_032 CONTENT { seed_key: 'messages-seed', code: 'FUN_032', title: 'Page not found', content: 'The page cannot be less than 1.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_032 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_032', code: 'FUN_032', title: 'Page not found', content: 'The page cannot be less than 1.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_033
UPSERT message:FUN_033 CONTENT { seed_key: 'messages-seed', code: 'FUN_033', title: 'Invalid type', content: 'The value of %s must be a valid integer greater than 1.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_033 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_033', code: 'FUN_033', title: 'Invalid type', content: 'The value of %s must be a valid integer greater than 1.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_034
UPSERT message:FUN_034 CONTENT { seed_key: 'messages-seed', code: 'FUN_034', title: 'Validator empty', content: 'No validators have been added.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_034 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_034', code: 'FUN_034', title: 'Validator empty', content: 'No validators have been added.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_035
UPSERT message:FUN_035 CONTENT { seed_key: 'messages-seed', code: 'FUN_035', title: 'Environment does not exist', content: 'The environment does not exist.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_035 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_035', code: 'FUN_035', title: 'Environment does not exist', content: 'The environment does not exist.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_036
UPSERT message:FUN_036 CONTENT { seed_key: 'messages-seed', code: 'FUN_036', title: 'Application does not exist', content: 'The application to which the environment is intended to be associated does not exist for the environment.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_036 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_036', code: 'FUN_036', title: 'Application does not exist', content: 'The application to which the environment is intended to be associated does not exist for the environment.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_037
UPSERT message:FUN_037 CONTENT { seed_key: 'messages-seed', code: 'FUN_037', title: 'Expiration date is earlier than today', content: 'The expiration date must be a date greater than today.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_037 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_037', code: 'FUN_037', title: 'Expiration date is earlier than today', content: 'The expiration date must be a date greater than today.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_038
UPSERT message:FUN_038 CONTENT { seed_key: 'messages-seed', code: 'FUN_038', title: 'Invalid Id', content: 'The id must not be the default UUID.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_038 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_038', code: 'FUN_038', title: 'Invalid Id', content: 'The id must not be the default UUID.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_039
UPSERT message:FUN_039 CONTENT { seed_key: 'messages-seed', code: 'FUN_039', title: 'Invalid date character', content: 'Date contains characters not allowed', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_039 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_039', code: 'FUN_039', title: 'Invalid date character', content: 'Date contains characters not allowed', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_040
UPSERT message:FUN_040 CONTENT { seed_key: 'messages-seed', code: 'FUN_040', title: 'Invalid Message code', content: 'The message code cannot be empty or null.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_040 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_040', code: 'FUN_040', title: 'Invalid Message code', content: 'The message code cannot be empty or null.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_041
UPSERT message:FUN_041 CONTENT { seed_key: 'messages-seed', code: 'FUN_041', title: 'Error searching for token', content: 'An error occurred while searching for the token.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_041 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_041', code: 'FUN_041', title: 'Error searching for token', content: 'An error occurred while searching for the token.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_042
UPSERT message:FUN_042 CONTENT { seed_key: 'messages-seed', code: 'FUN_042', title: 'Page out of range', content: 'Page number exceeds total pages %s.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_042 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_042', code: 'FUN_042', title: 'Page out of range', content: 'Page number exceeds total pages %s.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- FUN_043
UPSERT message:FUN_043 CONTENT { seed_key: 'messages-seed', code: 'FUN_043', title: 'Invalid value type', content: 'The value of %s cannot contain special characters.', type_id: message_type:functional, category_id: message_category:error, status_id: message_state:active, application: 'MessageUcoLab System', functionality_id: functionality:catalog_system };
UPSERT message_data_collection:FUN_043 CONTENT { seed_key: 'messages-seed', message_id: 'message:FUN_043', code: 'FUN_043', title: 'Invalid value type', content: 'The value of %s cannot contain special characters.', type: { name: 'Functional' }, category: { name: 'Error' }, status: { name: 'Active' }, application: 'MessageUcoLab System', functionality: { name: 'System Messages' } };

-- =============================================================
-- LINK ALL MESSAGES TO SYSTEM ENVIRONMENT (message_environment)
-- =============================================================

UPSERT message_environment:me_TCH_001 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_001, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_002 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_002, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_003 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_003, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_004 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_004, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_005 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_005, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_006 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_006, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_007 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_007, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_008 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_008, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_009 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_009, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_010 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_010, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_011 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_011, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_012 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_012, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_013 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_013, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_014 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_014, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_015 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_015, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_016 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_016, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_017 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_017, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_018 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_018, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_019 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_019, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_020 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_020, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_021 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_021, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_022 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_022, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_023 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_023, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_024 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_024, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_025 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_025, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_026 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_026, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_027 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_027, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_028 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_028, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_029 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_029, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_030 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_030, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_031 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_031, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_032 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_032, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_033 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_033, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_034 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_034, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_TCH_035 CONTENT { seed_key: 'messages-seed', message_id: message:TCH_035, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_001 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_001, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_002 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_002, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_003 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_003, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_004 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_004, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_005 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_005, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_006 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_006, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_007 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_007, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_008 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_008, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_009 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_009, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_010 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_010, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_011 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_011, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_012 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_012, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_013 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_013, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_014 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_014, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_015 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_015, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_017 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_017, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_018 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_018, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_019 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_019, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_020 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_020, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_021 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_021, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_022 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_022, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_023 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_023, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_024 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_024, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_025 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_025, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_026 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_026, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_027 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_027, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_028 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_028, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_029 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_029, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_030 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_030, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_031 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_031, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_032 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_032, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_033 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_033, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_034 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_034, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_035 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_035, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_036 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_036, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_037 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_037, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_038 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_038, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_039 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_039, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_040 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_040, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_041 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_041, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_042 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_042, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
UPSERT message_environment:me_FUN_043 CONTENT { seed_key: 'messages-seed', message_id: message:FUN_043, environment_id: environment:catalog_system, state_data_id: message_environment_state:active };
