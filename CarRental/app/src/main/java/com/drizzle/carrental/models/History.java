package com.drizzle.carrental.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class History {

    boolean isPaymentOrCoverage;

    Payment payment; //valid only when isPaymentOrCoverage is true

    Coverage coverage; //valid only when isPaymentOrCoverage is false



}
