package com.jaimedantas.fiitaxcalculator.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FiiDataPdf {

    String name;
    int client_id;
    List<FiiSubscricao> subscricoes;

    List<byte[]> corretagem;

}
