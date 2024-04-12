package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import lombok.Data;

@Data
public class ChildMessage {
    NewOrderEvent newOrder;
    DKTradeEvent dkTrade;
    ExecutionEvent execution;
    OrderCancelEvent orderCancel;
    OrderReplaceEvent orderReplace;
    OrderStatusEvent orderStatus;
    CancelRejectEvent cancelReject;
}
