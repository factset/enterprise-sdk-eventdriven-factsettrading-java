package com.factset.sdk.eventdriven.factsettrading.model.snapshots;

public enum ExecutionType {
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
