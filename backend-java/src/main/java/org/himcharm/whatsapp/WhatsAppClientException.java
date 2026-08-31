package org.himcharm.whatsapp;

public class WhatsAppClientException extends RuntimeException {

    public WhatsAppClientException(String message) {
        super(message);
    }

    public WhatsAppClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
