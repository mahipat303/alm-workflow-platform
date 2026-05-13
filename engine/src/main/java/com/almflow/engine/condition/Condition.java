package com.almflow.engine.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Workflow condition tree. Composite nodes ({@code All}, {@code Any}, {@code Not})
 * combine leaf predicates. Adding a new leaf type = one record + one entry below
 * + a case in {@link ConditionEvaluator}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Condition.All.class, name = "all"),
        @JsonSubTypes.Type(value = Condition.Any.class, name = "any"),
        @JsonSubTypes.Type(value = Condition.Not.class, name = "not"),
        @JsonSubTypes.Type(value = Condition.FieldTransition.class, name = "fieldTransition"),
        @JsonSubTypes.Type(value = Condition.FieldChanged.class, name = "fieldChanged")
})
public sealed interface Condition {

    record All(List<Condition> of) implements Condition {}

    record Any(List<Condition> of) implements Condition {}

    record Not(Condition of) implements Condition {}

    /** Matches when the event contains a change for {@code field} from {@code from} to {@code to}. */
    record FieldTransition(String field, String from, String to) implements Condition {}

    /** Matches when the event contains any change for {@code field}. */
    record FieldChanged(String field) implements Condition {}
}
