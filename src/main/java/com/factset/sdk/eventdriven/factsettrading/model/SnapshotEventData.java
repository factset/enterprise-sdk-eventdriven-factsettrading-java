package com.factset.sdk.eventdriven.factsettrading.model;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.InitialLoadType;
import com.factset.sdk.eventdriven.factsettrading.model.tradeevents.OrderUpdateEvent;
import com.factset.sdk.eventdriven.factsettrading.model.tradeevents.ParentOrderUpdateEvent;
import lombok.Data;

@Data
public class SnapshotEventData {
    Integer seqNo;
    Boolean isDone;
    String requestId;
    InitialLoadType initialLoadType;
    OrderUpdateEvent inboundOrderSnapshot;
    ParentOrderUpdateEvent parentOrderSnapshot;
    OrderUpdateEvent outboundOrderSnapshot;
}
