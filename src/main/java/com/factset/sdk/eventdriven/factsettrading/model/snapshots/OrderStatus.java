package com.factset.sdk.eventdriven.factsettrading.model.snapshots;

public enum OrderStatus {
    
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
