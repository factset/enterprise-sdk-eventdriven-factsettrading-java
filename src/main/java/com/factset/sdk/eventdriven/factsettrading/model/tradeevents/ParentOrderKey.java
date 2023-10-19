package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import lombok.Data;

@Data
public class ParentOrderKey {
    String basketId;
    String symbol;
}
