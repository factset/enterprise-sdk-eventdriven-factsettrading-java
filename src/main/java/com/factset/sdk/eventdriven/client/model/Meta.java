package com.factset.sdk.eventdriven.client.model;

import lombok.Data;

@Data
public class Meta {
    int id;
    String type;
    long timeout;

    public static Meta forClass(Class<?> model) {
        Meta meta = new Meta();
        meta.setType(model.getSimpleName());
        return meta;
    }
}
