package com.factset.sdk.eventdriven.factsettrading.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExecutionType {
    @JsonProperty("new")
    New,
    partialFill,
    fill,
    doneForDay,
    canceled,
    replaced,
    pendingCancel,
    stopped,
    rejected,
    suspended,
    pendingNew,
    calculated,
    expired,
    restated,
    pendingReplace
}
