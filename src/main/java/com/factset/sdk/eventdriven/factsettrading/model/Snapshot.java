package com.factset.sdk.eventdriven.factsettrading.model;
import lombok.Data;

@Data
public class Snapshot {
    Meta meta;
    SnapshotEventData data;
}
