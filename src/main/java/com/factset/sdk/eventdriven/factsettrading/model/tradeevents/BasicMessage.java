package com.factset.sdk.eventdriven.factsettrading.model.tradeevents;
import com.factset.sdk.eventdriven.factsettrading.model.tradeevents.repeatinggroup.RepeatingGroup;
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
    Map<String, RepeatingGroup> repeatingGroups;
    Map<Integer, String> userDefinedFields;
    String customFields;
}

