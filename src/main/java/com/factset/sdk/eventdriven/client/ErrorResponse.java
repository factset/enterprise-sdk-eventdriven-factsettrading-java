package com.factset.sdk.eventdriven.client;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    Meta meta = Meta.forClass(ErrorResponse.class);
    List<Error> errors;
}
