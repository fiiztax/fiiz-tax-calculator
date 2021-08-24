package com.jaimedantas.fiitaxcalculator.business;

import com.jaimedantas.fiitaxcalculator.enums.B3Taxes;
import com.jaimedantas.fiitaxcalculator.model.FiiData;
import com.jaimedantas.fiitaxcalculator.model.FiiTax;
import com.jaimedantas.fiitaxcalculator.ultils.CalculationEngine;

import java.math.BigDecimal;
import java.util.UUID;

public class TaxCalculator {

    public FiiTax calculeFiiTaxes(FiiData fiiData){

        FiiTax result = new FiiTax();

        result.setCorretoraFee(new BigDecimal("0"));
        result.setDate(fiiData.getDate());

        result.setName(fiiData.getName());
        result.setTransactionId(UUID.randomUUID().toString());

        //new calc for unit value
        BigDecimal averagePriceBought = CalculationEngine.divide8(fiiData.getTotalValueBought(), new BigDecimal(fiiData.getQuantityBought()));
        BigDecimal averagePriceSold = CalculationEngine.divide8(fiiData.getTotalValueSold(), new BigDecimal(fiiData.getQuantitySold()));


        BigDecimal realValueSold = CalculationEngine.multiply8(averagePriceSold, new BigDecimal(fiiData.getQuantitySold()));
        BigDecimal realValueBought = CalculationEngine.multiply8(averagePriceBought, new BigDecimal(fiiData.getQuantitySold()));

        BigDecimal netProfitValue = CalculationEngine.subtract(realValueSold, realValueBought);

        BigDecimal emolumentosTaxesBuyBase;
        BigDecimal liquidacaoTaxesBuyBase;
        if (fiiData.isFromSubscricao()){
            emolumentosTaxesBuyBase = new BigDecimal("0");
            liquidacaoTaxesBuyBase = new BigDecimal("0");
        } else{
            emolumentosTaxesBuyBase = CalculationEngine.multiply(realValueBought,B3Taxes.EMOLUMENTOS.atomicValue);
            liquidacaoTaxesBuyBase = CalculationEngine.multiply(realValueBought,B3Taxes.LIQUIDACAO.atomicValue);
        }
        BigDecimal emolumentosTaxesBuy = emolumentosTaxesBuyBase;
        BigDecimal liquidacaoTaxesBuy = liquidacaoTaxesBuyBase;
        BigDecimal emolumentosTaxesSell = CalculationEngine.multiply(realValueSold,B3Taxes.EMOLUMENTOS.atomicValue);
        BigDecimal liquidacaoTaxesSell = CalculationEngine.multiply(realValueSold,B3Taxes.LIQUIDACAO.atomicValue);
        BigDecimal IRRFTaxes = CalculationEngine.multiply(realValueSold,B3Taxes.IRRF.atomicValue);

        BigDecimal totalTaxes = CalculationEngine.add(emolumentosTaxesBuy,liquidacaoTaxesBuy, IRRFTaxes,
                emolumentosTaxesSell, liquidacaoTaxesSell);

        BigDecimal fixTax = CalculationEngine.multiply(
                CalculationEngine.subtract(netProfitValue, totalTaxes),
                B3Taxes.FIX_TAX.atomicValue);

        result.setEmolumentosFee(CalculationEngine.add(emolumentosTaxesBuy,emolumentosTaxesSell));
        result.setLiquidacaoFee(CalculationEngine.add(liquidacaoTaxesBuy,liquidacaoTaxesSell));
        result.setIRRFFee(IRRFTaxes);
        result.setFixedTax(fixTax);

        BigDecimal totalProfit = CalculationEngine.subtract(netProfitValue, fixTax);
        BigDecimal totalProfitPercentage = CalculationEngine.divide(totalProfit, CalculationEngine.add(fiiData.getTotalValueBought(),
                liquidacaoTaxesBuy, emolumentosTaxesBuy));

        result.setTotalTaxes(totalTaxes);
        result.setTotalProfitValue(totalProfit);
        result.setTotalProfitPercentage(totalProfitPercentage);
        result.setTotalTransactionIn(CalculationEngine.add(fiiData.getTotalValueBought(), emolumentosTaxesBuy, liquidacaoTaxesBuy));
        result.setTotalTransactionOut(CalculationEngine.add(fiiData.getTotalValueSold(), emolumentosTaxesSell, liquidacaoTaxesSell));

        return result;
    }


    @Deprecated
    public FiiTax calculeFiiTaxesManual(FiiData fiiData){

        FiiTax result = new FiiTax();

        result.setCorretoraFee(new BigDecimal("0"));

        result.setDate(fiiData.getDate());

        result.setName(fiiData.getName());
        result.setTransactionId(UUID.randomUUID().toString());

        BigDecimal netProfitValue = CalculationEngine.subtract(fiiData.getTotalValueSold(), fiiData.getTotalValueBought());

        BigDecimal emolumentosTaxesBuy = CalculationEngine.multiply(fiiData.getTotalValueBought(),B3Taxes.EMOLUMENTOS.atomicValue);
        BigDecimal liquidacaoTaxesBuy = CalculationEngine.multiply(fiiData.getTotalValueBought(),B3Taxes.LIQUIDACAO.atomicValue);
        BigDecimal emolumentosTaxesSell = CalculationEngine.multiply(fiiData.getTotalValueSold(),B3Taxes.EMOLUMENTOS.atomicValue);
        BigDecimal liquidacaoTaxesSell = CalculationEngine.multiply(fiiData.getTotalValueSold(),B3Taxes.LIQUIDACAO.atomicValue);
        BigDecimal IRRFTaxes = CalculationEngine.multiply(fiiData.getTotalValueSold(),B3Taxes.IRRF.atomicValue);

        BigDecimal totalTaxes = CalculationEngine.add(emolumentosTaxesBuy,liquidacaoTaxesBuy, IRRFTaxes,
                emolumentosTaxesSell, liquidacaoTaxesSell);

        BigDecimal fixTax = CalculationEngine.multiply(
                CalculationEngine.subtract(netProfitValue, totalTaxes),
                B3Taxes.FIX_TAX.atomicValue);

        result.setEmolumentosFee(CalculationEngine.add(emolumentosTaxesBuy,emolumentosTaxesSell));
        result.setLiquidacaoFee(CalculationEngine.add(liquidacaoTaxesBuy,liquidacaoTaxesSell));
        result.setIRRFFee(IRRFTaxes);
        result.setFixedTax(fixTax);

        BigDecimal totalProfit = CalculationEngine.subtract(netProfitValue, fixTax);
        BigDecimal totalProfitPercentage = CalculationEngine.divide(totalProfit, CalculationEngine.add(fiiData.getTotalValueBought(),
                liquidacaoTaxesBuy, emolumentosTaxesBuy));

        result.setTotalTaxes(totalTaxes);
        result.setTotalProfitValue(totalProfit);
        result.setTotalProfitPercentage(totalProfitPercentage);
        result.setTotalTransactionIn(CalculationEngine.add(fiiData.getTotalValueBought(), emolumentosTaxesBuy, liquidacaoTaxesBuy));
        result.setTotalTransactionOut(CalculationEngine.add(fiiData.getTotalValueSold(), emolumentosTaxesSell, liquidacaoTaxesSell));

        return result;
    }
}
