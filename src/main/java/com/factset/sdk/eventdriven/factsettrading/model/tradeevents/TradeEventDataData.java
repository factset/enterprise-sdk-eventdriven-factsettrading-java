package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import lombok.Data;

import java.util.List;

@Data
public class TradeEventDataData {

    Integer seqNo;
    String eventType;
    String firmName;
    String userName;
    String serial;

    //TODO check this could be an array
    List<OrderUpdateEvent> orders;
    ParentOrderUpdateEvent parentOrder;
    DKTradeEvent dkTrade;
    ExecutionEvent execution;
    OrderCancelEvent orderCancel;
    OrderReplaceEvent orderReplace;
    OrderStatusEvent orderStatus;
    CancelRejectEvent cancelReject;
}
