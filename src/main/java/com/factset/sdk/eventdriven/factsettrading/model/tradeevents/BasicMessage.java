package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import lombok.Data;
import java.util.Map;

@Data
public class BasicMessage {
    Integer messageSequenceNumber;
    String messageType;
    String possibleDuplicateFlag;
    String senderCompId;
    String senderSubId;
    String sendingTime;
    String targetCompId;
    String targetSubId;
    String possibleResend;
    String onBehalfOfCompId;
    String onBehalfOfSubId;
    String originalSendingTime;
    String deliverToCompId;
    String deliverToSubId;
    String senderLocationId;
    String targetLocationId;
    String onBehalfOfLocationId;
    String deliverToLocationId;
    String onBehalfOfSendingTime;
    String basketId;
    String waveId;
    Integer direction;
    Boolean transmit;
    String destName;
    Map<String, Map<Integer, Integer>> repeatingGroups;
    Map<String, Integer> userDefinedFields;
    String customFields;
}

