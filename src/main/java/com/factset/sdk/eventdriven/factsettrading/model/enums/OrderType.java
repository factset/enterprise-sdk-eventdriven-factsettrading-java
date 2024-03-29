package com.factset.sdk.eventdriven.factsettrading.model.enums;

public enum OrderType {
    market,
    limit,
    stop,
    stopLimit,
    marketOnClose,
    limitOrBetter,
    limitOnClose,
    peggedFunari,
    withOrWithout,
    limitWithOrWithout,
    onBasis,
    previouslyQuoted,
    previouslyIndicated,
    forexSwap,
    marketIfTouched,
    marketWithLeftOverAsLimit,
    previousFundValuationPoint,
    nextFundValuationPoint,
    counterOrderSelection
}
