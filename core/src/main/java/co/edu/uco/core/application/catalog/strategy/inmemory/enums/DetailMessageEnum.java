package co.edu.uco.core.application.catalog.strategy.inmemory.enums;

import co.edu.uco.core.application.catalog.MessageModel;
import lombok.Getter;

@Getter
public enum DetailMessageEnum {
    TCH_001(MessageKeyEnum.TCH_001, "Database not connected", "Unable to establish connection to %s database", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_002(MessageKeyEnum.TCH_002, "Message Broker not connected", "Unable to establish connection to Message Broker", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_003(MessageKeyEnum.TCH_003, "API Translate not connected", "Unable to establish connection to translate API", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_004(MessageKeyEnum.TCH_004, "Cache not connected", "Unable to establish connection to cache", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_005(MessageKeyEnum.TCH_005, "Parameters component not connected", "Unable to establish connection to Parameters component", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_006(MessageKeyEnum.TCH_006, "Security component not connected", "Unable to establish connection to Security component", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_007(MessageKeyEnum.TCH_007, "Key is null", "The message key is null", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_008(MessageKeyEnum.TCH_008, "Key is empty", "Message code does not exist", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_009(MessageKeyEnum.TCH_009, "Message not found", "Message code does not exist with %s key", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_010(MessageKeyEnum.TCH_010, "Code is required", "Code is required", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_011(MessageKeyEnum.TCH_011, "Content is required", "Content is required", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_012(MessageKeyEnum.TCH_012, "Title is required", "Title is required", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_013(MessageKeyEnum.TCH_013, "Type is required", "Type is required", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_014(MessageKeyEnum.TCH_014, "Category is required", "Category is required", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_015(MessageKeyEnum.TCH_015, "Message found in application", "Message with code {} in application {} found {}", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.INFORMATION),
    TCH_016(MessageKeyEnum.TCH_016, "Validation Error", "Validation error with correlation id {}", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_017(MessageKeyEnum.TCH_017, "Serializer not found", "No serializer was found for media type '{}' and there is no default serializer configured.", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_018(MessageKeyEnum.TCH_018, "Serializer error", "Error when serializing the object", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_019(MessageKeyEnum.TCH_019, "Error when serializing error response", "An error occurred while trying to serialize the error response.", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_020(MessageKeyEnum.TCH_020, "Response Error", "The error response is: {}", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_021(MessageKeyEnum.TCH_021, "Response success", "The successful response is: {}", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.INFORMATION),
    TCH_022(MessageKeyEnum.TCH_022, "Media type not supported", "The media type %s is not supported by the system", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_023(MessageKeyEnum.TCH_023, "Incorrect Media type", "Media type is not supported {}", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_024(MessageKeyEnum.TCH_024, "KeyPair generation failed", "An error occurred while generating the key pair", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_025(MessageKeyEnum.TCH_025, "Error generating token", "An error occurred while trying to create the token", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_026(MessageKeyEnum.TCH_026, "Error generating keys", "An error occurred while trying to create the token", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_027(MessageKeyEnum.TCH_027, "Error generating signature", "An error occurred while trying to generating the signature", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_028(MessageKeyEnum.TCH_028, "Error verifying access", "An error occurred while trying to verify access with the private key {} , the signature {} and the secret {} provided", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_029(MessageKeyEnum.TCH_029, "Error request to Doppler", "An error occurred sending request to Doppler", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_030(MessageKeyEnum.TCH_030, "Error response code", "The error code of the response is %s: ", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_031(MessageKeyEnum.TCH_031, "Access denied", "Access denied, the token is invalid", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_032(MessageKeyEnum.TCH_032, "Token header dont send", "Access denied, the header 'Token' has not been send", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_033(MessageKeyEnum.TCH_033, "Token expired", "Access denied, the token is expired or inactive", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_034(MessageKeyEnum.TCH_034, "SHA-256 not available", "SHA-256 algorithm not available", MessageTypeEnum.TECHNICAL, MessageCategoryEnum.ERROR),
    TCH_035(MessageKeyEnum.TCH_035, "Environment id does not exits", "The environment with the id %s provided does not exist.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_001(MessageKeyEnum.FUN_001, "Invalid message code", "The message code is invalid or not within allowed values", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_002(MessageKeyEnum.FUN_002, "Title is empty or null", "The message must have a valid title", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_003(MessageKeyEnum.FUN_003, "Content is empty or null", "The message must include a valid description", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_004(MessageKeyEnum.FUN_004, "Message type is null", "The message type is not defined", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_005(MessageKeyEnum.FUN_005, "Message category is null", "The message category is not defined", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_006(MessageKeyEnum.FUN_006, "Message not found in cache", "The message was not found in cache, we proceed to search in database", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.INFORMATION),
    FUN_007(MessageKeyEnum.FUN_007, "Message found in cache", "Messages were found in the database, proceed to return and cache.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.INFORMATION),
    FUN_008(MessageKeyEnum.FUN_008, "Cache and database messages are not the same amount.", "The number of messages in cache and database does not match, the cache is filled with the missing messages.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.WARNING),
    FUN_009(MessageKeyEnum.FUN_009, "Message found in cache", "Messages were found in cache, proceed to return.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_010(MessageKeyEnum.FUN_010, "Null Validator", "%s is required", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.INFORMATION),
    FUN_011(MessageKeyEnum.FUN_011, "Application not found", "Could not get the messages from the application, verify that the application exists.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_012(MessageKeyEnum.FUN_012, "Message not found", "There is no message with the code %s for the application %s", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_013(MessageKeyEnum.FUN_013, "Cache not connected", "Failed to connect to Redis", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_014(MessageKeyEnum.FUN_014, "Error during cache connection", "Data access exception while connecting to Redis", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_015(MessageKeyEnum.FUN_015, "Unexpected exception", "Unexpected exception while connecting to Redis", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_017(MessageKeyEnum.FUN_017, "Content be empty", "The content cannot be empty", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_018(MessageKeyEnum.FUN_018, "Title size exceeds limit", "The size of the content must not be less than 10", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_019(MessageKeyEnum.FUN_019, "Title size insufficient", "The size of the content cannot be larger than 100", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_020(MessageKeyEnum.FUN_020, "Content size insufficient", "The size of the title cannot be smaller than 10", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_021(MessageKeyEnum.FUN_021, "Content size exceeds limit", "The size of the title cannot be larger than 50", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_022(MessageKeyEnum.FUN_022, "Title be empty", "The title cannot be empty", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_023(MessageKeyEnum.FUN_023, "Unexpected Error", "An unexpected error has occurred", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_024(MessageKeyEnum.FUN_024, "information consulted", "%s", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.CONFIRMATION),
    FUN_025(MessageKeyEnum.FUN_025, "Error create token", "An error occurred while verify the token, please try again later", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_026(MessageKeyEnum.FUN_026, "Token not found", "The token is not found", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_027(MessageKeyEnum.FUN_027, "information consulted", "%s",MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.CONFIRMATION),
    FUN_028(MessageKeyEnum.FUN_028, "Invalid page number", "The page number must be between 1 and %d.",MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_029(MessageKeyEnum.FUN_029, "Invalid page size", "The page size must be between 1 and %d.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_030(MessageKeyEnum.FUN_030, "Invalid sort column", "The sort column '%s' is not valid.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_031(MessageKeyEnum.FUN_031, "Invalid sort direction", "The sort direction must be 'ASC' or 'DESC'.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_032(MessageKeyEnum.FUN_032, "Page not found", "The page cannot be less than 1.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_033(MessageKeyEnum.FUN_033, "Invalid %s type", "The value of %s must be a valid integer greater than 1.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_034(MessageKeyEnum.FUN_034, "Validator empty", "No validators have been added.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_035(MessageKeyEnum.FUN_035, "Environment does not exist", "The environment does not exist.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_036(MessageKeyEnum.FUN_036, "Application does not exist", "The application to which the environment is intended to be associated does not exist for the environment.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_037(MessageKeyEnum.FUN_037, "Expiration date is earlier than today", "The expiration date must be a date greater than today.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_038(MessageKeyEnum.FUN_038, "Invalid Id", "The id must not be the default UUID.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_039(MessageKeyEnum.FUN_039, "Invalid date character", "Date contains characters not allowed", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_040(MessageKeyEnum.FUN_040, "Invalid Message code", "The message code cannot be empty or null.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_041(MessageKeyEnum.FUN_041, "Error searching for token", "An error occurred while searching for the token.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_042(MessageKeyEnum.FUN_042, "Page out of range", "Page number exceeds total pages %s.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR),
    FUN_043(MessageKeyEnum.FUN_043, "Invalid %s type", "The value of %s cannot contain special characters.", MessageTypeEnum.FUNCTIONAL, MessageCategoryEnum.ERROR);
    private MessageKeyEnum code;
    private String title;
    private String content;
    private MessageTypeEnum type;
    private MessageCategoryEnum category;
    DetailMessageEnum(final MessageKeyEnum code, final String title, final String content, final MessageTypeEnum type, final MessageCategoryEnum category) {
        this.code = code;
        this.title = title;
        this.content = content;
        this.type = type;
        this.category = category;
    }
    public MessageModel getMessage() {
        return new MessageModel(code, content, title, type, category);
    }
}