package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;

import com.factset.sdk.eventdriven.factsettrading.model.enums.*;
import lombok.Data;

@Data
public class ExecutionEvent {
    BasicMessage basicMessage;
    String account;
    Double averagePrice;
    String clientOrderId;
    Double commission;
    CommissionType commissionType;
    String currency;
    Double cumulativeQuantity;
    String executionId;
    ExecutionInstruction executionInstruction;
    String executionReferenceId;
    ExecutionTransactionType executionTransactionType;
    IdSource idSource;
    String lastCapacity;
    String lastMarket;
    Double lastPrice;
    Double lastQuantity;
    String orderId;
    Double orderQuantity;
    OrderStatus orderStatus;
    OrderType orderType;
    String originalClientOrderId;
    Double price;
    Rule80A rule80A;
    String securityId;
    Side side;
    String symbol;
    String text;
    TimeInForce timeInForce;
    String transactTime;
    SettlementType settlementType;
    String settlementDate;
    String symbolSuffix;
    String listId;
    String tradeDate;
    String executingBroker;
    Double stopPrice;
    Integer orderRejectReason;
    String issuer;
    String securityDescription;
    String clientId;
    Double minimumQuantity;
    ReportToExchange reportToExchange;
    String settlementCurrencyAmount;
    String settlementCurrency;
    String expireTime;
    ExecutionType executionType;
    Double leavesQuantity;
    Double settlementCurrencyFxRate;
    String settlementCurrencyFxRateCalc;
    Double orderQuantity2;
    SecurityType securityType;
    String settlementDate2;
    Double lastSpotRate;
    Double lastForwardPoIntegers;
    String secondaryOrderId;
    String maturityMonthYear;
    PutOrCall putOrCall;
    Double strikePrice;
    String maturityDay;
    String optionAttribute;
    String securityExchange;
    Double pegDifference;
    Integer executionRestatementReason;
    String expireDate;
    Double price2;
    Double lastForwardPoIntegers2;
    String ticketId;
    Double referenceFillPrice;
    Double referenceFillQuantity;
}

