package co.edu.uco.infraestructure.config;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;

public final class InfrastructureConstant {
    private InfrastructureConstant() {}
    public static final String COLLECTION_TOKEN = CatalogPortStaticRef.getMessage("FUN_074");
    public static final String COLLECTION_TOKEN_STATE = CatalogPortStaticRef.getMessage("FUN_075");
    public static final String COLLECTION_MESSAGE_ENVIRONMENT = CatalogPortStaticRef.getMessage("FUN_076");
    public static final String COLLECTION_ENVIRONMENT = CatalogPortStaticRef.getMessage("FUN_077");
    public static final String COLLECTION_APPLICATION = CatalogPortStaticRef.getMessage("FUN_078");
    public static final String COLLECTION_STATUS_MESSAGE_ENVIRONMENT = CatalogPortStaticRef.getMessage("FUN_079");
    public static final String COLLECTION_ENVIRONMENT_TYPE = CatalogPortStaticRef.getMessage("FUN_080");
    public static final String COLLECTION_REPRESENT_PARAMETER = CatalogPortStaticRef.getMessage("FUN_081");
    public static final String COLLECTION_PARAMETER = CatalogPortStaticRef.getMessage("FUN_082");

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = CatalogPortStaticRef.getMessage("FUN_083");
    public static final String FIELD_CREATION_DATE = CatalogPortStaticRef.getMessage("FUN_084");
    public static final String FIELD_EXPIRATION_DATE = CatalogPortStaticRef.getMessage("FUN_085");
    public static final String FIELD_ENVIRONMENT_ID = CatalogPortStaticRef.getMessage("FUN_086");
    public static final String FIELD_MESSAGE_ENVIRONMENT_ID = CatalogPortStaticRef.getMessage("FUN_087");
    public static final String FIELD_MESSAGE = CatalogPortStaticRef.getMessage("FUN_088");
    public static final String FIELD_SECRET_NAME = CatalogPortStaticRef.getMessage("FUN_089");
    public static final String FIELD_STATE_ID = CatalogPortStaticRef.getMessage("FUN_090");
    public static final String FIELD_TYPE_ID = CatalogPortStaticRef.getMessage("FUN_091");
    public static final String FIELD_APPLICATION_ID = CatalogPortStaticRef.getMessage("FUN_092");
    public static final String ENVIRONMENT_ID_ATTRIBUTE = CatalogPortStaticRef.getMessage("FUN_093");
    public static final String TOKEN_ENTITY = CatalogPortStaticRef.getMessage("FUN_094");
    public static final String TOKEN_STATE_ACTIVE_ID = CatalogPortStaticRef.getMessage("FUN_095");
    public static final String TOKEN_STATE_INACTIVE_ID = CatalogPortStaticRef.getMessage("FUN_144");

    public static final String PACKAGE_BASE = "co.edu.uco";
    public static final String PACKAGE_REPOSITORY_ADAPTER = "co.edu.uco.infraestructure.secondaryadapters.repository";

    public static final int CACHE_EXPIRATION_TIME = 15;
    public static final int CACHE_MAXIMUM_SIZE = 500;

    public static final long MICROSECONDS_PER_MILLISECOND = 1_000;

    public static final String PULSAR_CONFIG_PREFIX = "pulsar";

    public static final String HTML_OPEN_TAG = CatalogPortStaticRef.getMessage("FUN_096");
    public static final String HTML_CLOSE_TAG = CatalogPortStaticRef.getMessage("FUN_097");
    public static final String BODY_OPEN_TAG = CatalogPortStaticRef.getMessage("FUN_098");
    public static final String BODY_CLOSE_TAG = CatalogPortStaticRef.getMessage("FUN_099");
    public static final String PRE_OPEN_TAG = CatalogPortStaticRef.getMessage("FUN_100");
    public static final String PRE_CLOSE_TAG = CatalogPortStaticRef.getMessage("FUN_101");

    public static final String REDIS_HASH = "Message";
    public static final String CACHE_REDIS_ADAPTER = "messageRedisAdapter";
    public static final String CORRELATION_ID = CatalogPortStaticRef.getMessage("FUN_102");
    public static final String LOGGING_REQUEST_URI = CatalogPortStaticRef.getMessage("FUN_103");
    public static final String LOGGING_HTTP_METHOD = CatalogPortStaticRef.getMessage("FUN_104");
    public static final String LOGGING_SESSION_ID = CatalogPortStaticRef.getMessage("FUN_105");
    public static final String LOGGING_QUERY_STRING = CatalogPortStaticRef.getMessage("FUN_106");
    public static final String LOGGING_PARAMETER_APPLICATION_NAME = CatalogPortStaticRef.getMessage("FUN_107");
    public static final String LOGGING_PARAMETER_CODE_MESSAGE = CatalogPortStaticRef.getMessage("FUN_108");
    public static final String LOGGING_PARAMETER_APPLICATION = CatalogPortStaticRef.getMessage("FUN_109");
    public static final String LOGGING_TIMESTAMP = CatalogPortStaticRef.getMessage("FUN_110");
    public static final String LOGGING_THREAD = CatalogPortStaticRef.getMessage("FUN_111");
    public static final String LOGGING_APP_NAME = CatalogPortStaticRef.getMessage("FUN_112");
    public static final String REQUEST_GET_HEADER_ACCEPT = CatalogPortStaticRef.getMessage("FUN_113");
    public static final String REQUEST_GET_HEADER_TOKEN = CatalogPortStaticRef.getMessage("FUN_114");
    public static final String REQUEST_GET_HEADER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_115");
    public static final String REQUEST_GET_HEADER_AUTHORIZATION = CatalogPortStaticRef.getMessage("FUN_116");
    public static final String MEDIA_TYPE_DEFAULT = CatalogPortStaticRef.getMessage("FUN_117");
    public static final String JSON_SERIALIZER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_118");
    public static final String BEARER_TOKEN = CatalogPortStaticRef.getMessage("FUN_119");
    public static final String YAML_SERIALIZER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_120");
    public static final String HTML_SERIALIZER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_121");
    public static final String TEXT_SERIALIZER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_122");
    public static final String XML_SERIALIZER_CONTENT_TYPE = CatalogPortStaticRef.getMessage("FUN_123");
    public static final String PATTERN_TIMESTAMP_FORMAT = CatalogPortStaticRef.getMessage("FUN_124");
    public static final int PAIR_KEY_SIZE = 2048;
    public static final String ALGORITHM_GENERATE_PAIR_KEY = CatalogPortStaticRef.getMessage("FUN_125");
    public static final String ALGORITHM_PAIR_KEY = CatalogPortStaticRef.getMessage("FUN_126");
    public static final String DOPPLER_DTO_SECRET_NAME = CatalogPortStaticRef.getMessage("FUN_127");
    public static final String DOPPLER_DTO_PRIVATE_KEY = CatalogPortStaticRef.getMessage("FUN_128");
    public static final String DOPPLER_DTO_NAME = "name";
    public static final String DOPPLER_DTO_VALUE = "value";
    public static final String DOPPLER_DTO_RAW = CatalogPortStaticRef.getMessage("FUN_129");
    public static final String DOPPLER_CONFIG_PREFIX = "doppler";
    public static final String WEB_CONFIG_API_MESSAGE = CatalogPortStaticRef.getMessage("FUN_130");
    public static final String WEB_CONFIG_API_APPLICATION = CatalogPortStaticRef.getMessage("FUN_131");
    public static final String WEB_CONFIG_API_ENVIRONMENT = CatalogPortStaticRef.getMessage("FUN_132");
    public static final String WEB_CONFIG_API_MESSAGE_LIST = CatalogPortStaticRef.getMessage("FUN_133");
    public static final String WEB_CONFIG_API_MESSAGE_CODE = CatalogPortStaticRef.getMessage("FUN_134");
    public static final String WEB_CONFIG_API_MESSAGE_CODE_TRANSLATION = CatalogPortStaticRef.getMessage("FUN_143");

    public static final String SWAGGER_UI_HTML = CatalogPortStaticRef.getMessage("FUN_135");
    public static final String SWAGGER_UI = CatalogPortStaticRef.getMessage("FUN_136");
    public static final String SWAGGER_RESOURCES = CatalogPortStaticRef.getMessage("FUN_137");
    public static final String SWAGGER_API_DOCS = CatalogPortStaticRef.getMessage("FUN_138");
    public static final String SWAGGER_WEBJARS = CatalogPortStaticRef.getMessage("FUN_139");

    public static final String MESSAGE_CODE_PARAMETER = "messageCode";

    public static final String SURREAL_CONFIG_PREFIX = "surreal";
    public static final String SURREAL_ADAPTER = CatalogPortStaticRef.getMessage("FUN_140");

    public static final String TOKEN_SURREAL_ADAPTER = "tokenSurrealAdapter";
    public static final String TOKEN_STATE_SURREAL_ADAPTER = "tokenStateSurrealAdapter";

    public static final String SURREAL_TABLE_TOKEN = CatalogPortStaticRef.getMessage("FUN_141");
    public static final String SURREAL_TABLE_TOKEN_STATE = CatalogPortStaticRef.getMessage("FUN_142");
}
