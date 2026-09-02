package org.himcharm.whatsapp;

public class WhatsAppClientException extends RuntimeException {

    private final String errorCode;

    public WhatsAppClientException(String message) {
        super(message);
        this.errorCode = null;
    }

    public WhatsAppClientException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public WhatsAppClientException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
