package com.jaimedantas.fiitaxcalculator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiiData {

    int clientId;
    String name;
    boolean fromSubscricao;

    long quantityBought;
    long quantitySold;

    BigDecimal purchasePriceUnit;
    BigDecimal soldPriceUnit;

    BigDecimal totalValueBought;
    BigDecimal totalValueSold;

    Date date;

}
