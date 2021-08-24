package com.jaimedantas.fiitaxcalculator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class FiiSubscricao {
    String transactionId;
    String name;
    String corretora;
    Date date;
    long quantityBought;
    BigDecimal totalBought;
}
