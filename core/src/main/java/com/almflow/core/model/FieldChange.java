package com.almflow.core.model;

import java.time.Instant;

public record FieldChange(
        String fieldName,
        Object fromValue,
        Object toValue,
        String changedBy,
        Instant changedAt
) {
}
