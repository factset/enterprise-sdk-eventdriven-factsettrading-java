package com.factset.sdk.eventdriven.factsettrading.model;

import com.factset.sdk.eventdriven.model.Meta;
import lombok.Data;

@Data
public class OrderUpdateEvent {
    Meta meta;

    OrderUpdateEventData data;
}
