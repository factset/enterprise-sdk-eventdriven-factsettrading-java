package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import com.factset.sdk.eventdriven.factsettrading.model.enums.OrderType;
import com.factset.sdk.eventdriven.factsettrading.model.enums.Side;
import com.factset.sdk.eventdriven.factsettrading.model.enums.TimeInForce;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ParentOrderUpdateEvent {
    ParentOrderKey id;
    OrderType orderType;
    Side side;
    Double totalQuantity;
    String customer;
    String account;
    String strategy;
    Map<String, Double> accountAllocation;
    List<String> parentTicketIds;
    String country;
    String currency;
    String sector;
    String industry;
    String listingInfo;
    String userGroup;
    Double userQuantity;
    Double userPrice;
    String userDestination;
    Boolean onHold;
    Double strikePrice;
    Double strike2;
    Double strike3;
    Double maximumPrice;
    Double sentBid;
    Double sentAsk;
    Integer sentBidSize;
    Integer sentAskSize;
    Map<String, String> udfMap;
    Double stopPrice;
    String expireTime;
    Double committedQuantity;
    String status;
    Double executedQuantity;
    Double executedValue;
    Double averagePrice;
    TimeInForce timeInForce;
    String creationTime;
    String directedBrokers;
    String restrictedBrokers;
}

