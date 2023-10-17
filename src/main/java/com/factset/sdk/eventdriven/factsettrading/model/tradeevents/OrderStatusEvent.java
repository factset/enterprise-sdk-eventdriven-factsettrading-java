package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import com.factset.sdk.eventdriven.factsettrading.model.snapshots.IdSource;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.PutOrCall;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.SecurityType;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.Side;
import lombok.Data;

@Data
public class OrderStatusEvent {
    BasicMessage basicMessage;
    String clientOrderId;
    IdSource idSource;
    String orderId;
    Double price;
    String securityId;
    Side side;
    String symbol;
    String symbolSuffix;
    String issuer;
    String executingBroker;
    String securityDescription;
    String securityExchange;
    String clientId;
    SecurityType securityType;
    String maturityMonthYear;
    PutOrCall putOrCall;
    Double strikePrice;
    String maturityDay;
    String optionAttribute;
    String ticketId;
    String status;
    Boolean statusReceived;
    Double orderQuantity;
}
