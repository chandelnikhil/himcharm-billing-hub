package org.himcharm.utilies;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Base64UrlCodec {

    private Base64UrlCodec() {
    }

    public static String encode(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value to encode must not be null");
        }
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Encoded value must not be blank");
        }
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
