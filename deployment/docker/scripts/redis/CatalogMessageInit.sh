#!/bin/sh

set -e

echo "Waiting for Redis..."

until redis-cli \
  -h "$REDISHOST" \
  -p "$REDISPORT" \
  -a "$REDISPASSWORD" \
  PING | grep -q PONG
do
  sleep 2
done

echo "Redis is available. Loading message catalog..."

redis_hset() {
  redis-cli \
    -h "$REDISHOST" \
    -p "$REDISPORT" \
    -a "$REDISPASSWORD" \
    HSET "$1" code "$1" title "$2" content "$3" type "$4" category "$5"
}

# =============================================================
# Technical Messages
# =============================================================

redis_hset "TCH_001" "Database not connected" "Unable to establish connection to %s database" "TECHNICAL" "ERROR"
redis_hset "TCH_002" "Message Broker not connected" "Unable to establish connection to Message Broker" "TECHNICAL" "ERROR"
redis_hset "TCH_003" "API Translate not connected" "Unable to establish connection to translate API" "TECHNICAL" "ERROR"
redis_hset "TCH_004" "Cache not connected" "Unable to establish connection to cache" "TECHNICAL" "ERROR"
redis_hset "TCH_005" "Parameters component not connected" "Unable to establish connection to Parameters component" "TECHNICAL" "ERROR"
redis_hset "TCH_006" "Security component not connected" "Unable to establish connection to Security component" "TECHNICAL" "ERROR"
redis_hset "TCH_007" "Key is null" "The message key is null" "TECHNICAL" "ERROR"
redis_hset "TCH_008" "Key is empty" "Message code does not exist" "TECHNICAL" "ERROR"
redis_hset "TCH_009" "Message not found" "Message code does not exist with %s key" "TECHNICAL" "ERROR"
redis_hset "TCH_010" "Code is required" "Code is required" "TECHNICAL" "ERROR"
redis_hset "TCH_011" "Content is required" "Content is required" "TECHNICAL" "ERROR"
redis_hset "TCH_012" "Title is required" "Title is required" "TECHNICAL" "ERROR"
redis_hset "TCH_013" "Type is required" "Type is required" "TECHNICAL" "ERROR"
redis_hset "TCH_014" "Category is required" "Category is required" "TECHNICAL" "ERROR"
redis_hset "TCH_015" "Message found in application" "Message with code {} in application {} found {}" "TECHNICAL" "INFORMATION"
redis_hset "TCH_016" "Validation Error" "Validation error with correlation id {}" "TECHNICAL" "ERROR"
redis_hset "TCH_017" "Serializer not found" "No serializer was found for media type '{}' and there is no default serializer configured." "TECHNICAL" "ERROR"
redis_hset "TCH_018" "Serializer error" "Error when serializing the object" "TECHNICAL" "ERROR"
redis_hset "TCH_019" "Error when serializing error response" "An error occurred while trying to serialize the error response." "TECHNICAL" "ERROR"
redis_hset "TCH_020" "Response Error" "The error response is: {}" "TECHNICAL" "ERROR"
redis_hset "TCH_021" "Response success" "The successful response is: {}" "TECHNICAL" "INFORMATION"
redis_hset "TCH_022" "Media type not supported" "The media type %s is not supported by the system" "TECHNICAL" "ERROR"
redis_hset "TCH_023" "Incorrect Media type" "Media type is not supported {}" "TECHNICAL" "ERROR"
redis_hset "TCH_024" "KeyPair generation failed" "An error occurred while generating the key pair" "TECHNICAL" "ERROR"
redis_hset "TCH_025" "Error generating token" "An error occurred while trying to create the token" "TECHNICAL" "ERROR"
redis_hset "TCH_026" "Error generating keys" "An error occurred while trying to create the token" "TECHNICAL" "ERROR"
redis_hset "TCH_027" "Error generating signature" "An error occurred while trying to generating the signature" "TECHNICAL" "ERROR"
redis_hset "TCH_028" "Error verifying access" "An error occurred while trying to verify access with the private key {} , the signature {} and the secret {} provided" "TECHNICAL" "ERROR"
redis_hset "TCH_029" "Error request to Doppler" "An error occurred sending request to Doppler" "TECHNICAL" "ERROR"
redis_hset "TCH_030" "Error response code" "The error code of the response is %s: " "TECHNICAL" "ERROR"
redis_hset "TCH_031" "Access denied" "Access denied, the token is invalid" "TECHNICAL" "ERROR"
redis_hset "TCH_032" "Token header dont send" "Access denied, the header 'Token' has not been send" "TECHNICAL" "ERROR"
redis_hset "TCH_033" "Token expired" "Access denied, the token is expired or inactive" "TECHNICAL" "ERROR"
redis_hset "TCH_034" "SHA-256 not available" "SHA-256 algorithm not available" "TECHNICAL" "ERROR"
redis_hset "TCH_035" "Environment id does not exits" "The environment with the id %s provided does not exist." "FUNCTIONAL" "ERROR"

# =============================================================
# Functional Messages
# =============================================================

redis_hset "FUN_001" "Invalid message code" "The message code is invalid or not within allowed values" "FUNCTIONAL" "ERROR"
redis_hset "FUN_002" "Title is empty or null" "The message must have a valid title" "FUNCTIONAL" "ERROR"
redis_hset "FUN_003" "Content is empty or null" "The message must include a valid description" "FUNCTIONAL" "ERROR"
redis_hset "FUN_004" "Message type is null" "The message type is not defined" "FUNCTIONAL" "ERROR"
redis_hset "FUN_005" "Message category is null" "The message category is not defined" "FUNCTIONAL" "ERROR"
redis_hset "FUN_006" "Message not found in cache" "The message was not found in cache, we proceed to search in database" "FUNCTIONAL" "INFORMATION"
redis_hset "FUN_007" "Message found in cache" "Messages were found in the database, proceed to return and cache." "FUNCTIONAL" "INFORMATION"
redis_hset "FUN_008" "Cache and database messages are not the same amount." "The number of messages in cache and database does not match, the cache is filled with the missing messages." "FUNCTIONAL" "WARNING"
redis_hset "FUN_009" "Message found in cache" "Messages were found in cache, proceed to return." "FUNCTIONAL" "ERROR"
redis_hset "FUN_010" "Null Validator" "%s is required" "FUNCTIONAL" "INFORMATION"
redis_hset "FUN_011" "Application not found" "Could not get the messages from the application, verify that the application exists." "FUNCTIONAL" "ERROR"
redis_hset "FUN_012" "Message not found" "There is no message with the code %s for the application %s" "FUNCTIONAL" "ERROR"
redis_hset "FUN_013" "Cache not connected" "Failed to connect to Redis" "FUNCTIONAL" "ERROR"
redis_hset "FUN_014" "Error during cache connection" "Data access exception while connecting to Redis" "FUNCTIONAL" "ERROR"
redis_hset "FUN_015" "Unexpected exception" "Unexpected exception while connecting to Redis" "FUNCTIONAL" "ERROR"
redis_hset "FUN_017" "Content be empty" "The content cannot be empty" "FUNCTIONAL" "ERROR"
redis_hset "FUN_018" "Title size exceeds limit" "The size of the content must not be less than 10" "FUNCTIONAL" "ERROR"
redis_hset "FUN_019" "Title size insufficient" "The size of the content cannot be larger than 100" "FUNCTIONAL" "ERROR"
redis_hset "FUN_020" "Content size insufficient" "The size of the title cannot be smaller than 10" "FUNCTIONAL" "ERROR"
redis_hset "FUN_021" "Content size exceeds limit" "The size of the title cannot be larger than 50" "FUNCTIONAL" "ERROR"
redis_hset "FUN_022" "Title be empty" "The title cannot be empty" "FUNCTIONAL" "ERROR"
redis_hset "FUN_023" "Unexpected Error" "An unexpected error has occurred" "FUNCTIONAL" "ERROR"
redis_hset "FUN_024" "information consulted" "%s" "FUNCTIONAL" "CONFIRMATION"
redis_hset "FUN_025" "Error create token" "An error occurred while verify the token, please try again later" "FUNCTIONAL" "ERROR"
redis_hset "FUN_026" "Token not found" "The token is not found" "FUNCTIONAL" "ERROR"
redis_hset "FUN_027" "information consulted" "%s" "FUNCTIONAL" "CONFIRMATION"
redis_hset "FUN_028" "Invalid page number" "The page number must be between 1 and %d." "FUNCTIONAL" "ERROR"
redis_hset "FUN_029" "Invalid page size" "The page size must be between 1 and %d." "FUNCTIONAL" "ERROR"
redis_hset "FUN_030" "Invalid sort column" "The sort column '%s' is not valid." "FUNCTIONAL" "ERROR"
redis_hset "FUN_031" "Invalid sort direction" "The sort direction must be 'ASC' or 'DESC'." "FUNCTIONAL" "ERROR"
redis_hset "FUN_032" "Page not found" "The page cannot be less than 1." "FUNCTIONAL" "ERROR"
redis_hset "FUN_033" "Invalid %s type" "The value of %s must be a valid integer greater than 1." "FUNCTIONAL" "ERROR"
redis_hset "FUN_034" "Validator empty" "No validators have been added." "FUNCTIONAL" "ERROR"
redis_hset "FUN_035" "Environment does not exist" "The environment does not exist." "FUNCTIONAL" "ERROR"
redis_hset "FUN_036" "Application does not exist" "The application to which the environment is intended to be associated does not exist for the environment." "FUNCTIONAL" "ERROR"
redis_hset "FUN_037" "Expiration date is earlier than today" "The expiration date must be a date greater than today." "FUNCTIONAL" "ERROR"
redis_hset "FUN_038" "Invalid Id" "The id must not be the default UUID." "FUNCTIONAL" "ERROR"
redis_hset "FUN_039" "Invalid date character" "Date contains characters not allowed" "FUNCTIONAL" "ERROR"
redis_hset "FUN_040" "Invalid Message code" "The message code cannot be empty or null." "FUNCTIONAL" "ERROR"
redis_hset "FUN_041" "Error searching for token" "An error occurred while searching for the token." "FUNCTIONAL" "ERROR"
redis_hset "FUN_042" "Page out of range" "Page number exceeds total pages %s." "FUNCTIONAL" "ERROR"
redis_hset "FUN_043" "Invalid %s type" "The value of %s cannot contain special characters." "FUNCTIONAL" "ERROR"
redis_hset "FUN_044" "Target language required" "The target language is required." "FUNCTIONAL" "ERROR"
redis_hset "FUN_045" "Target language invalid" "The target language is invalid." "FUNCTIONAL" "ERROR"
redis_hset "FUN_046" "Translation disabled" "Dynamic translation is disabled." "FUNCTIONAL" "ERROR"
redis_hset "FUN_047" "Translation API key required" "Dynamic translation needs TRANSLATION_AI_API_KEY." "FUNCTIONAL" "ERROR"
redis_hset "FUN_048" "Translation failed" "The message could not be translated dynamically." "FUNCTIONAL" "ERROR"
redis_hset "FUN_049" "Token not found" "The token was not found in the database." "FUNCTIONAL" "ERROR"
redis_hset "FUN_050" "Invalid UUID format" "The UUID to be converted has no valid format." "FUNCTIONAL" "ERROR"
redis_hset "FUN_051" "UUID conversion error" "An unexpected error occurred while trying to convert the entry to UUID." "FUNCTIONAL" "ERROR"
redis_hset "FUN_052" "Invalid date format" "The date to be converted has no valid format." "FUNCTIONAL" "ERROR"
redis_hset "FUN_053" "Unexpected error" "An unexpected error has occurred." "FUNCTIONAL" "ERROR"

echo "Message catalog loaded successfully."