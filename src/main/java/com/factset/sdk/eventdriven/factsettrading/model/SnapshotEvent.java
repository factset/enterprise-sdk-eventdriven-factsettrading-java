package com.factset.sdk.eventdriven.factsettrading.model;
import lombok.Data;

@Data
public class SnapshotEvent {
    Meta meta;
    SnapshotEventData data;
}
