package com.factset.sdk.streaming.factsettrading.model;

import lombok.Data;

@Data
public class OrderUpdateEvent {
    Meta meta;

    OrderUpdateEventData data;
}
