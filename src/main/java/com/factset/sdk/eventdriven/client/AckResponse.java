package com.factset.sdk.eventdriven.client;

import lombok.Data;

@Data
class AckResponse {
    Meta meta = Meta.forClass(AckResponse.class);
}
