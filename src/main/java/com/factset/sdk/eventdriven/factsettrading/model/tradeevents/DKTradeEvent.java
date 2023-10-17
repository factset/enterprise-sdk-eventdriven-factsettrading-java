package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import com.factset.sdk.eventdriven.factsettrading.model.snapshots.IdSource;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.SecurityType;
import com.factset.sdk.eventdriven.factsettrading.model.snapshots.Side;
import lombok.Data;

@Data
public class DKTradeEvent {
    BasicMessage basicMessage;
    String executionId;
    IdSource idSource;
    Double lastPrice;
    Double lastQuantity;
    String orderId;
    Double orderQuantity;
    String securityId;
    Side side;
    String symbol;
    String text;
    String symbolSuffix;
    String issuer;
    String securityDescription;
    String dontKnowReason;
    Double cashOrderQuantity;
    SecurityType securityType;
    String ticketId;
}
