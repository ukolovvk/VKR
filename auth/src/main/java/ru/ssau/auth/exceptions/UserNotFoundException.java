package ru.ssau.auth.exceptions;

/**
 * @author ukolov-victor
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
