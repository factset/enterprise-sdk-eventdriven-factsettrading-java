package com.factset.sdk.eventdriven.factsettrading.model;

import lombok.Data;

@Data
public class OrderUpdateEvent {
    Meta meta;

    OrderUpdateEventData data;
}
