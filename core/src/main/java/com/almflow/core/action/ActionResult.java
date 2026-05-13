package com.almflow.core.action;

public record ActionResult(
        boolean success,
        String message,
        String externalReference
) {
    public static ActionResult ok(String externalReference) {
        return new ActionResult(true, "ok", externalReference);
    }

    public static ActionResult failure(String message) {
        return new ActionResult(false, message, null);
    }
}
