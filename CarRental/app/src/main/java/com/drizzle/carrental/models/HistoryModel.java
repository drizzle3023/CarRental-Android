package com.drizzle.carrental.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryModel {

    boolean isPaymentOrClaim;

    Payment payment; //valid only when isPaymentOrClaim is true

    Claim claim; //valid only when isPaymentOrClaim is false



}
