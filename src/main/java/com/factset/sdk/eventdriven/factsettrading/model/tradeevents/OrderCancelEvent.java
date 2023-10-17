package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import lombok.Data;

@Data
public class OrderCancelEvent {
    BasicMessage basicMessage;
    String account;
    String clientOrderId;
    String idSource;
    String orderId;
    Double orderQuantity;
    String originalClientOrderId;
    Double price;
    String securityId;
    String side;
    String symbol;
    String text;
    String transactionTime;
    String symbolSuffix;
    String listId;
    String executingBroker;
    String waveNo;
    String issuer;
    String securityDescription;
    String clientId;
    String cancelType;
    Double cashOrderQuantity;
    String securityType;
    String maturityMonthYear;
    String putOrCall;
    Double strikePrice;
    String maturityDay;
    String optionAttribute;
    String securityExchange;
    String ticketId;
    String status;
}

