package co.edu.uco.core.application.catalog.strategy.inmemory.enums;

import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.helper.UtilText;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MessageKeyEnum {
    TCH_001("TCH_001", MessageSourceEnum.CRITICAL),
    TCH_002("TCH_002", MessageSourceEnum.CRITICAL),
    TCH_003("TCH_003", MessageSourceEnum.CRITICAL),
    TCH_004("TCH_004", MessageSourceEnum.CRITICAL),
    TCH_005("TCH_005", MessageSourceEnum.CRITICAL),
    TCH_006("TCH_006", MessageSourceEnum.CRITICAL),
    TCH_007("TCH_007", MessageSourceEnum.CRITICAL),
    TCH_008("TCH_008", MessageSourceEnum.CRITICAL),
    TCH_009("TCH_009", MessageSourceEnum.CRITICAL),
    TCH_010("TCH_010", MessageSourceEnum.CRITICAL),
    TCH_011("TCH_011", MessageSourceEnum.CRITICAL),
    TCH_012("TCH_012", MessageSourceEnum.CRITICAL),
    TCH_013("TCH_013", MessageSourceEnum.CRITICAL),
    TCH_014("TCH_014", MessageSourceEnum.CRITICAL),
    TCH_015("TCH_015", MessageSourceEnum.CRITICAL),
    TCH_016("TCH_016", MessageSourceEnum.CRITICAL),
    TCH_017("TCH_017", MessageSourceEnum.CRITICAL),
    TCH_018("TCH_018", MessageSourceEnum.CRITICAL),
    TCH_019("TCH_019", MessageSourceEnum.CRITICAL),
    TCH_020("TCH_020", MessageSourceEnum.CRITICAL),
    TCH_021("TCH_021", MessageSourceEnum.CRITICAL),
    TCH_022("TCH_022", MessageSourceEnum.CRITICAL),
    TCH_023("TCH_023", MessageSourceEnum.CRITICAL),
    TCH_024("TCH_024", MessageSourceEnum.CRITICAL),
    TCH_025("TCH_025", MessageSourceEnum.CRITICAL),
    TCH_026("TCH_026", MessageSourceEnum.CRITICAL),
    TCH_027("TCH_027", MessageSourceEnum.CRITICAL),
    TCH_028("TCH_028", MessageSourceEnum.CRITICAL),
    TCH_029("TCH_029", MessageSourceEnum.NETWORK),
    TCH_030("TCH_030", MessageSourceEnum.NETWORK),
    TCH_031("TCH_031", MessageSourceEnum.NETWORK),
    TCH_032("TCH_032", MessageSourceEnum.NETWORK),
    TCH_033("TCH_033", MessageSourceEnum.NETWORK),
    TCH_034("TCH_034", MessageSourceEnum.NETWORK),
    TCH_035("TCH_035", MessageSourceEnum.CRITICAL),
    FUN_001("FUN_001", MessageSourceEnum.CRITICAL),
    FUN_002("FUN_002", MessageSourceEnum.CRITICAL),
    FUN_003("FUN_003", MessageSourceEnum.CRITICAL),
    FUN_004("FUN_004", MessageSourceEnum.CRITICAL),
    FUN_005("FUN_005", MessageSourceEnum.CRITICAL),
    FUN_006("FUN_006", MessageSourceEnum.CACHE),
    FUN_007("FUN_007", MessageSourceEnum.DATABASE),
    FUN_008("FUN_008", MessageSourceEnum.DATABASE),
    FUN_009("FUN_009", MessageSourceEnum.CACHE),
    FUN_010("FUN_010", MessageSourceEnum.FILE),
    FUN_011("FUN_011", MessageSourceEnum.CRITICAL),
    FUN_012("FUN_012", MessageSourceEnum.CRITICAL),
    FUN_013("FUN_013", MessageSourceEnum.CACHE),
    FUN_014("FUN_014", MessageSourceEnum.CACHE),
    FUN_015("FUN_015", MessageSourceEnum.CACHE),
    FUN_016("FUN_016", MessageSourceEnum.CRITICAL),
    FUN_017("FUN_017", MessageSourceEnum.CRITICAL),
    FUN_018("FUN_018", MessageSourceEnum.CRITICAL),
    FUN_019("FUN_019", MessageSourceEnum.CRITICAL),
    FUN_020("FUN_020", MessageSourceEnum.CRITICAL),
    FUN_021("FUN_021", MessageSourceEnum.CRITICAL),
    FUN_022("FUN_022", MessageSourceEnum.CRITICAL),
    FUN_023("FUN_023", MessageSourceEnum.CRITICAL),
    FUN_024("FUN_024", MessageSourceEnum.NETWORK),
    FUN_025("FUN_025", MessageSourceEnum.NETWORK),
    FUN_026("FUN_026", MessageSourceEnum.NETWORK),
    FUN_027("FUN_027", MessageSourceEnum.NETWORK),
    FUN_028("FUN_028", MessageSourceEnum.CRITICAL),
    FUN_029("FUN_029", MessageSourceEnum.CRITICAL),
    FUN_030("FUN_030", MessageSourceEnum.CRITICAL),
    FUN_031("FUN_031", MessageSourceEnum.CRITICAL),
    FUN_032("FUN_032", MessageSourceEnum.CRITICAL),
    FUN_033("FUN_033", MessageSourceEnum.CRITICAL),
    FUN_034("FUN_034", MessageSourceEnum.CRITICAL),
    FUN_035("FUN_035", MessageSourceEnum.CRITICAL),
    FUN_036("FUN_036", MessageSourceEnum.CRITICAL),
    FUN_037("FUN_037", MessageSourceEnum.CRITICAL),
    FUN_038("FUN_038", MessageSourceEnum.CRITICAL),
    FUN_039("FUN_039", MessageSourceEnum.CRITICAL),
    FUN_040("FUN_040", MessageSourceEnum.CRITICAL),
    FUN_041("FUN_041", MessageSourceEnum.CRITICAL),
    FUN_042("FUN_042", MessageSourceEnum.CRITICAL),
    FUN_043("FUN_043", MessageSourceEnum.CRITICAL);
    private final String key;
    private final MessageSourceEnum source;
    MessageKeyEnum(String key, MessageSourceEnum source) {
        this.key = key;
        this.source = source;
    }
    public static MessageKeyEnum of(String key) {
        if (UtilText.isEmptyOrNull(key)) {
            throw CrossWordsException.build("Message code cannot be empty");
        }
        return Arrays.stream(MessageKeyEnum.values()).filter(messageCatalogEnum
                        -> messageCatalogEnum.getKey().equals(key)).findFirst()
                .orElseThrow(() -> CrossWordsException.build("Message code does not exist: " + key));
    }
}