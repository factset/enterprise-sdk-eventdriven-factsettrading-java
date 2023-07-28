package com.factset.sdk.eventdriven.factsettrading.model;

import lombok.Data;

@Data
public class OrderUpdateEventData {
    String ordStatus;
    String symbol;
    String side;
    String senderSubID;
    Double cumQty;
    String sendTime;
    String messageType;
    Double avgPx;
    Double price;
    Double orderQty;
    String timeInForce;
    String account;
    String ordType;
    String ticketId;
}
