package com.primefuel.fuelguard.platform.shared.application.result;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ApplicationError(
        String code,
        String message,
        String details) {

    public ApplicationError(String code, String message) {
        this(code, message, null);
    }
}
