package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import lombok.Data;

@Data
public class DKTradeEvent {
    BasicMessage basicMessage;
    String executionId;
    String idSource;
    Double lastPrice;
    Double lastQuantity;
    String orderId;
    Double orderQuantity;
    String securityId;
    String side;
    String symbol;
    String text;
    String symbolSuffix;
    String issuer;
    String securityDescription;
    String dontKnowReason;
    Double cashOrderQuantity;
    String securityType;
    String ticketId;
}
