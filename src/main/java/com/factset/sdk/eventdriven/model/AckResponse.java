package com.factset.sdk.eventdriven.model;

import lombok.Data;

@Data
public class AckResponse {
    Meta meta = Meta.forClass(AckResponse.class);
}
