package com.factset.sdk.streaming.client;

import lombok.Data;

@Data
class AckResponse {
    Meta meta = Meta.forClass(AckResponse.class);
}
