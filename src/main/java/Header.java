import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Header {
    PHOTO_REQUEST("SPH"),
    PHOTO("PHT"),
    REGISTER("REG"),
    AUTHENTICATE("AUT"),
    BAD_CREDENTIALS("FCK"),
    UNRECOGNISED("WTF");

    @Getter
    String value;

    public static Header fromString(String text) {
        for (Header s : Header.values()) {
            if (s.value.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException(text + " is invalid TaskStatus name");
    }
}
