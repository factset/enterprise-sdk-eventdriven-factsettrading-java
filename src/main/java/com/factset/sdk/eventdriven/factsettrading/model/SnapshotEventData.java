package com.factset.sdk.eventdriven.factsettrading.model;
import com.factset.sdk.eventdriven.factsettrading.model.enums.InitialLoadType;
import com.factset.sdk.eventdriven.factsettrading.model.tradeevents.OrderUpdateEvent;
import com.factset.sdk.eventdriven.factsettrading.model.tradeevents.ParentOrderUpdateEvent;
import lombok.Data;

@Data
public class SnapshotEventData {
    Integer seqNo;
    String requestId;
    OrderUpdateEvent inboundOrderSnapshot;
    ParentOrderUpdateEvent parentOrderSnapshot;
    OrderUpdateEvent childOrderSnapshot;
}
