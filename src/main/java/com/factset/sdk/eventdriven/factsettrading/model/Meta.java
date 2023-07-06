package com.factset.sdk.eventdriven.factsettrading.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Meta {
    int id;
    String type;
    long timeout;

    public Meta(String type) {
        this.type = type;
    }
}
