package com.factset.sdk.eventdriven.factsettrading.model;
import lombok.Data;

@Data
public class TradeEvent {

    Meta meta;
    TradeEventData data;
}
