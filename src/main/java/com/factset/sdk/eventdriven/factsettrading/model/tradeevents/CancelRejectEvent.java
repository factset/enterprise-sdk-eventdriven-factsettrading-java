package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import com.factset.sdk.eventdriven.factsettrading.model.enums.OrderStatus;
import lombok.Data;

@Data
public class CancelRejectEvent {
    BasicMessage basicMessage;
    String account;
    String clientOrderId;
    String orderId;
    OrderStatus orderStatus;
    String originalClientOrderId;
    String symbol;
    String text;
    String transactionTime;
    String listId;
    String executingBroker;
    String cancelRejectReason;
    String clientId;
    String secondaryOrderId;
    String cancelRejectResponseTo;
    Double orderQuantity;
    String ticketId;
    Double price;
    String status;
    String optionAttribute;
    Integer encodedTextLength;
    Double replacedPrice;
    String encodedText;
}

