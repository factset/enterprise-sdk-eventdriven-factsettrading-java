package com.factset.sdk.eventdriven.factsettrading.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    
    @JsonProperty("new")
    New,
    partiallyFilled,
    filled,
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
    acceptedForBidding,
    pendingReplace
}
