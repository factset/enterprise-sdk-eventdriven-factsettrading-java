package com.factset.sdk.eventdriven.factsettrading.model.tradeevents.repeatinggroup;

import lombok.Data;

import java.util.List;

@Data
public class GroupEntry{
    List<TagValuePair> value;
}
