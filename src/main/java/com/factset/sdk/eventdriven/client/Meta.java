package com.factset.sdk.eventdriven.client;

import lombok.Data;

@Data
class Meta {
    int id;
    String type;
    long timeout;

    static Meta forClass(Class<?> model) {
        Meta meta = new Meta();
        meta.setType(model.getSimpleName());
        return meta;
    }
}
