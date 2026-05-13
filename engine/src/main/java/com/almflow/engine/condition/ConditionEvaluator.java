package com.almflow.engine.condition;

import com.almflow.core.event.EntityChangedEvent;
import com.almflow.core.model.FieldChange;

import java.util.Objects;

public final class ConditionEvaluator {

    private ConditionEvaluator() {}

    public static boolean evaluate(Condition condition, EntityChangedEvent event) {
        return switch (condition) {
            case Condition.All all -> all.of().stream().allMatch(c -> evaluate(c, event));
            case Condition.Any any -> any.of().stream().anyMatch(c -> evaluate(c, event));
            case Condition.Not not -> !evaluate(not.of(), event);
            case Condition.FieldTransition t -> matchesTransition(event, t);
            case Condition.FieldChanged c -> event.changes().stream()
                    .anyMatch(ch -> ch.fieldName().equalsIgnoreCase(c.field()));
        };
    }

    private static boolean matchesTransition(EntityChangedEvent event, Condition.FieldTransition t) {
        for (FieldChange change : event.changes()) {
            if (!change.fieldName().equalsIgnoreCase(t.field())) continue;
            boolean fromOk = t.from() == null || Objects.equals(asString(change.fromValue()), t.from());
            boolean toOk = t.to() == null || Objects.equals(asString(change.toValue()), t.to());
            if (fromOk && toOk) return true;
        }
        return false;
    }

    private static String asString(Object o) {
        return o == null ? null : o.toString();
    }
}
