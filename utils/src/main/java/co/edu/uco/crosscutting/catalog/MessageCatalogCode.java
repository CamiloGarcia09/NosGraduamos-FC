package co.edu.uco.crosscutting.catalog;

/**
 * Enumerador centralizado con todos los códigos de mensajes del catálogo
 * de la aplicación. Provee un único punto de referencia para cada código,
 * eliminando strings literales dispersos y garantizando type-safety.
 *
 * <p>Convención de prefijos:</p>
 * <ul>
 *   <li>{@code TCH_} — Mensajes técnicos (infraestructura, errores de sistema)</li>
 *   <li>{@code FUN_} — Mensajes funcionales (negocio, usuario)</li>
 * </ul>
 *
 * <p>Uso: {@code catalogPort.getMessage(MessageCatalogCode.TCH_041.getCode())}</p>
 */
public enum MessageCatalogCode {

    // =========================================================================
    // Mensajes técnicos — Broker / Pulsar
    // =========================================================================

    /** Error de conexión o comunicación con el broker de mensajería. */
    TCH_002("TCH_002"),

    // =========================================================================
    // Mensajes técnicos — Presenter / HTTP
    // =========================================================================

    /** Error al presentar la respuesta HTTP. */
    TCH_016("TCH_016"),

    /** Error interno al escribir la respuesta al cliente. */
    TCH_019("TCH_019"),

    /** Error al serializar la respuesta de error. */
    TCH_020("TCH_020"),

    /** Respuesta exitosa enviada al cliente. */
    TCH_021("TCH_021"),

    // =========================================================================
    // Mensajes técnicos — Cifrado / Seguridad
    // =========================================================================

    /** Error al generar el par de claves RSA. */
    TCH_026("TCH_026"),

    /** Error al generar la firma con la clave pública. */
    TCH_027("TCH_027"),

    /** Error al verificar acceso con clave privada y firma. */
    TCH_028("TCH_028"),

    // =========================================================================
    // Mensajes técnicos — Proveedor de secretos (Doppler)
    // =========================================================================

    /** Error general al obtener el secreto desde Doppler. */
    TCH_029("TCH_029"),

    /** Respuesta HTTP no exitosa al consultar Doppler. */
    TCH_030("TCH_030"),

    // =========================================================================
    // Mensajes técnicos — Traducción IA (LangChain4j)
    // =========================================================================

    /** Traducción completada exitosamente. */
    TCH_036("TCH_036"),

    /** Error al intentar traducir un mensaje con el modelo IA. */
    TCH_037("TCH_037"),

    // =========================================================================
    // Mensajes técnicos — Broker (SendBrokerMessage)
    // =========================================================================

    /** El dominio del mensaje enviado al broker es nulo. */
    TCH_038("TCH_038"),

    /** El mensaje serializado para el broker está vacío o es inválido. */
    TCH_039("TCH_039"),

    // =========================================================================
    // Mensajes técnicos — Cifrado (validación de argumentos)
    // =========================================================================

    /** Datos o clave pública nulos al generar firma. */
    TCH_040("TCH_040"),

    // =========================================================================
    // Mensajes técnicos — Azure Key Vault
    // =========================================================================

    /** La URL del Azure Key Vault no está configurada. */
    TCH_041("TCH_041"),

    /** Error al construir el cliente de Azure Key Vault. */
    TCH_042("TCH_042"),

    /** El nombre del secreto solicitado está vacío o es nulo. */
    TCH_043("TCH_043"),

    /** El cliente de Azure Key Vault no está inicializado. */
    TCH_044("TCH_044"),

    /** El secreto obtenido de Azure Key Vault está vacío o es nulo. */
    TCH_045("TCH_045"),

    /** El secreto de Azure Key Vault está deshabilitado. */
    TCH_046("TCH_046"),

    /** El secreto no fue encontrado en Azure Key Vault. */
    TCH_047("TCH_047"),

    /** Error de autenticación al acceder a Azure Key Vault. */
    TCH_048("TCH_048"),

    /** Error HTTP al comunicarse con Azure Key Vault. */
    TCH_049("TCH_049"),

    /** Error inesperado al obtener el secreto de Azure Key Vault. */
    TCH_050("TCH_050"),

    // =========================================================================
    // Mensajes técnicos — Catálogo Redis
    // =========================================================================

    /** Error al leer el modelo de mensaje del catálogo en Redis. */
    TCH_051("TCH_051"),

    /** Error al leer el contenido del mensaje del catálogo en Redis. */
    TCH_052("TCH_052"),

    /** Error al leer el título del mensaje del catálogo en Redis. */
    TCH_053("TCH_053"),

    /** Error al escribir el mensaje en el catálogo de Redis. */
    TCH_054("TCH_054"),

    // =========================================================================
    // Mensajes funcionales — Caché de mensajes Redis
    // =========================================================================

    /** Error de acceso a datos al guardar en la caché de mensajes. */
    FUN_014("FUN_014"),

    /** Error inesperado al guardar en la caché de mensajes. */
    FUN_015("FUN_015"),

    // =========================================================================
    // Mensajes funcionales — Error genérico de usuario
    // =========================================================================

    /** Mensaje funcional genérico de error presentado al usuario. */
    FUN_023("FUN_023"),

    // =========================================================================
    // Mensajes funcionales — Cifrado / Seguridad
    // =========================================================================

    /** Mensaje funcional de error al generar o verificar claves de cifrado. */
    FUN_025("FUN_025"),

    // =========================================================================
    // Mensajes funcionales — Traducción IA
    // =========================================================================

    /** Traducción deshabilitada en la configuración. */
    FUN_046("FUN_046"),

    /** API key de OpenAI no configurada. */
    FUN_047("FUN_047"),

    /** Error al traducir el mensaje con el modelo IA. */
    FUN_048("FUN_048"),

    /** Plantilla del prompt enviado al modelo IA para traducción. */
    FUN_054("FUN_054"),

    /** Nombre del esquema JSON de respuesta del modelo IA. */
    FUN_055("FUN_055"),

    /** Propiedad 'translatedTitle' del esquema JSON de respuesta IA. */
    FUN_056("FUN_056"),

    /** Propiedad 'translatedContent' del esquema JSON de respuesta IA. */
    FUN_057("FUN_057"),

    /** Nombre del proveedor OpenAI para mostrar al usuario. */
    FUN_058("FUN_058"),

    /** Nombre del proveedor Ollama/custom para mostrar al usuario. */
    FUN_059("FUN_059");

    // =========================================================================

    private final String code;

    MessageCatalogCode(String code) {
        this.code = code;
    }

    /**
     * Retorna el código de mensaje tal como está definido en el catálogo.
     *
     * @return el código del mensaje (p.ej. {@code "TCH_041"})
     */
    public String getCode() {
        return code;
    }
}
