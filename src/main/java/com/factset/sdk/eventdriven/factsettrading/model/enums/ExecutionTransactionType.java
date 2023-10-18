package com.factset.sdk.eventdriven.factsettrading.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExecutionTransactionType {
    @JsonProperty("new")
    New,
    cancel,
    correct,
    status
}
