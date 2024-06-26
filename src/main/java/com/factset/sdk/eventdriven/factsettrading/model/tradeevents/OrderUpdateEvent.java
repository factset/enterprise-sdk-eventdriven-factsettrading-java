package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import com.factset.sdk.eventdriven.factsettrading.model.enums.*;
import lombok.Data;

@Data
public class OrderUpdateEvent {
    BasicMessage basicMessage;
    String account;
    String clientOrderId;
    Double commission;
    CommissionType commissionType;
    String currency;
    ExecutionInstruction executionInstruction;
    HandlingInstruction handlingInstructions;
    IdSource idSource;
    String ioiId;
    String orderId;
    Double orderQuantity;
    String orderStatus;
    OrderType orderType;
    String originalClientOrderId;
    Double price;
    Rule80A rule80A;
    String securityId;
    String symbol;
    Side side;
    String text;
    TimeInForce timeInForce;
    String transactTime;
    SettlementType settlementType;
    String settlementDate;
    String symbolSuffix;
    String executingBroker;
    OpenClose openClose;
    String processCode;
    Double stopPrice;
    String executionDestination;
    String issuer;
    String securityDescription;
    String clientId;
    Double minimumQuantity;
    Double maximumFloor;
    ReportToExchange reportToExchange;
    Boolean locateRequired;
    String quoteId;
    String settlementCurrency;
    ForexRequest forexRequest;
    String expireTime;
    Double prevClosePrice;
    Double cashOrderQuantity;
    String effectiveTime;
    SecurityType securityType;
    Double orderQuantity2;
    String settlementDate2;
    String maturityMonthYear;
    PutOrCall putOrCall;
    Double strikePrice;
    CoveredOrUncovered coveredOrUncovered;
    CustomerOrFirm customerOrFirm;
    String maturityDay;
    String optionAttribute;
    String securityExchange;
    Double maximumShow;
    Double pegDifference;
    DiscretionInstruction discretionInstruction;
    Double discretionOffset;
    String expireDate;
    Product product;
    String cfiCode;
    Double price2;
    Integer pegSelectionIndex;
    CancelType cancelType;
    Double bidPrice;
    Double askPrice;
    Double lastPrice;
    Integer bidSize;
    Integer askSize;
    String ticketId;
    Double executedQuantity;
    Double averagePrice;
    Double leavesQuantity;
    Double executedValue;
    ChildMessage[] childMessage;
    ChildMessage[] inboundMessage;
    Boolean containsAllChildMessages;
}
