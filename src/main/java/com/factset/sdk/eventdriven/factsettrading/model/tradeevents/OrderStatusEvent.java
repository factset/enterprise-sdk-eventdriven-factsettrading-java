package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import lombok.Data;

@Data
public class OrderStatusEvent {
    BasicMessage basicMessage;
    String clientOrderId;
    String idSource;
    String orderId;
    Double price;
    String securityId;
    String side;
    String symbol;
    String symbolSuffix;
    String issuer;
    String executingBroker;
    String securityDescription;
    String securityExchange;
    String clientId;
    String securityType;
    String maturityMonthYear;
    String putOrCall;
    Double strikePrice;
    String maturityDay;
    String optionAttribute;
    String ticketId;
    String status;
    Boolean statusReceived;
    Double orderQuantity;
}
