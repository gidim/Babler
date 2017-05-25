package edu.columbia.main.language_id.com.detectLanguage.errors;

@SuppressWarnings("serial")
public class APIError extends Exception {
    public int code;

    public APIError(String message, int code) {
        super(message);
        this.code = code;
    }
}