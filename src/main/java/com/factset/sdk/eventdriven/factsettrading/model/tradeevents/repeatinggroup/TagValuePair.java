package com.factset.sdk.eventdriven.factsettrading.model.tradeevents.repeatinggroup;

import lombok.Data;

@Data
public class TagValuePair{
    int tag;
    MessageValue value;
}
