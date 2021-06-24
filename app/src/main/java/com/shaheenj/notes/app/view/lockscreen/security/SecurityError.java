package com.shaheenj.notes.app.view.lockscreen.security;

public class SecurityError {

    private final String mMessage;
    private final Integer mCode;

    /**
     * Constructor.
     * @param message exception message.
     * @param code error code.
     */
    SecurityError(String message, Integer code) {
        mMessage = message;
        mCode = code;
    }

    /**
     * Get error message.
     * @return error message.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Get error code.
     * @return error code.
     */
    public Integer getCode() {
        return mCode;
    }
}
