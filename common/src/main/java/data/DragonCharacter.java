package data;

import java.io.Serializable;

/**
 * Перечисление содержащее все качества характера дракона
 *
 * @author steepikk
 */

public enum DragonCharacter implements Serializable {
    GOOD,
    CHAOTIC_EVIL,
    FICKLE;

    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var colorType : values()) {
            nameList.append(colorType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
