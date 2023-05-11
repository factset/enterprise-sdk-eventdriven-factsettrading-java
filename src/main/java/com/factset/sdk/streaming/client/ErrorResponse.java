package com.factset.sdk.streaming.client;

import lombok.Data;

import java.util.List;

@Data
class ErrorResponse {
    Meta meta = Meta.forClass(ErrorResponse.class);
    List<Error> errors;
}
