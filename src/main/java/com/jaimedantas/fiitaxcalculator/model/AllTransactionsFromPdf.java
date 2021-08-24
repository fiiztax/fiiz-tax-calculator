package com.jaimedantas.fiitaxcalculator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
public class AllTransactionsFromPdf {
    LinkedList<IndividualTransaction> finalTransactionsPurchase;
    LinkedList<IndividualTransaction> finalTransactionsSale;
}
