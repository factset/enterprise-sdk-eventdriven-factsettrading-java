package com.factset.sdk.eventdriven.model;

import com.factset.sdk.eventdriven.client.Error;
import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    Meta meta = Meta.forClass(ErrorResponse.class);
    List<Error> errors;
}
