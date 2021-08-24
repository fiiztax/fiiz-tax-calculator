package com.jaimedantas.fiitaxcalculator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
public class IndividualTransaction {

    String name;
    boolean fromSubscricao;
    String productType;
    long totalUnit;
    BigDecimal totalValue;
    String transactionType;
    Date date;

}
