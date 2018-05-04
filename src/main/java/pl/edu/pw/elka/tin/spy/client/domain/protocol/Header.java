package pl.edu.pw.elka.tin.spy.client.domain.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Header {
    PHOTO_REQUEST("SPH"),
    PHOTO("PHT"),
    REGISTRATION_REQUEST("REG"),
    SUCCESSFUL_REGISTRATION("ROK"),
    SUCCESSFUL_AUTH("AOK"),
    AUTH_FAILED("FCK"),
    AUTHENTICATION_REQUEST("AUT"),
    UNRECOGNISED("WTF");

    @Getter
    String value;

    public boolean equals(Header header) {
        return value.equals(header.getValue());
    }

    public static Header fromString(String text) {
        for (Header s : Header.values()) {
            if (s.value.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException(text + " is invalid TaskStatus name");
    }
}
