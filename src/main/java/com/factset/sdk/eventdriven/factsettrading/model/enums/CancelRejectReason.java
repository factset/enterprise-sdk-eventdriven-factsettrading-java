package com.factset.sdk.eventdriven.factsettrading.model.enums;

public enum CancelRejectReason {
    tooLateToCancel,
    unknownOrder,
    brokerCredit,
    orderAlreadyInPendingStatus,
    unableToProcessOrderMassCancelRequest,
    origOrdModTime,
    duplicateClOrdId,
    priceExceedsCurrentPrice,
    priceExceedsCurrentPriceBand,
    invalidPriceIncrement,
    other
}
