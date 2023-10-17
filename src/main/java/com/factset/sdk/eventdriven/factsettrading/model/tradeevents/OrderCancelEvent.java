package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import com.factset.sdk.eventdriven.factsettrading.model.enums.IdSource;
import com.factset.sdk.eventdriven.factsettrading.model.enums.PutOrCall;
import com.factset.sdk.eventdriven.factsettrading.model.enums.SecurityType;
import com.factset.sdk.eventdriven.factsettrading.model.enums.Side;
import lombok.Data;

@Data
public class OrderCancelEvent {
    BasicMessage basicMessage;
    String account;
    String clientOrderId;
    IdSource idSource;
    String orderId;
    Double orderQuantity;
    String originalClientOrderId;
    Double price;
    String securityId;
    Side side;
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
    SecurityType securityType;
    String maturityMonthYear;
    PutOrCall putOrCall;
    Double strikePrice;
    String maturityDay;
    String optionAttribute;
    String securityExchange;
    String ticketId;
    String status;
}

