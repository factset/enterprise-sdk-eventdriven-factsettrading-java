package com.factset.sdk.eventdriven.factsettrading.model.enums;

public enum ExecutionInstruction {
    notHeld,
    work,
    goAlong,
    overTheDay,
    held,
    participateDoNotInitiate,
    strictScale,
    tryToScale,
    stayOnBidSide,
    stayOnOfferSide,
    noCross,
    oKToCross,
    callFirst,
    percentOfVolume,
    doNotIncrease,
    doNotReduce,
    allOrNone,
    reinstateOnSystemFailure,
    institutionsOnly,
    reinstateOnTradingHalt,
    cancelOnTradingHalt,
    lastPeg,
    midPricePeg,
    nonNegotiable,
    openingPeg,
    marketPeg,
    cancelOnSystemFailure,
    primaryPeg,
    suspend,
    customerDisplayInstruction,
    netting,
    pegToVWAP,
    tradeAlong,
    tryToStop,
    cancelIfNotBest,
    trailingStopPeg,
    strictLimit,
    ignorePriceValidityChecks,
    pegToLimitPrice,
    workToTargetStrategy
}
