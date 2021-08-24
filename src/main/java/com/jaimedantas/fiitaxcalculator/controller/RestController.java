package com.jaimedantas.fiitaxcalculator.controller;

import com.jaimedantas.fiitaxcalculator.business.TaxCalculator;
import com.jaimedantas.fiitaxcalculator.mapper.PdfToFiiDataMonth;
import com.jaimedantas.fiitaxcalculator.model.*;
import com.jaimedantas.fiitaxcalculator.storage.StaticStorage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.jaimedantas.fiitaxcalculator.mapper.PdfToSubscricao.convertToSubscricao;
import static com.jaimedantas.fiitaxcalculator.mapper.TransactionsToFiiTax.convertToFiiTax;


@Controller
@RequestMapping(path="/tax")
@Api("Tax Calculator for Real Estate Funds")
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    @ApiOperation("Calculates de total taxes for FII")
    @CrossOrigin(origins = "*")
    @PostMapping(path="/fii/manual", produces = "application/json", consumes = "application/json")
    public @ResponseBody FiiTax calculateFiiTaxesManual (@RequestBody FiiData fii,
                                                         @RequestHeader("fiiz-tax-api-key") String header) throws Throwable {

        //not implement yet
        if (fii.getTotalValueBought().equals(new BigDecimal(0)) && fii.getTotalValueSold().equals(new BigDecimal(0))){
            throw new Throwable("Unit price not implemented yet");
        }

        logger.info(fii.toString());
        TaxCalculator taxCalculator = new TaxCalculator();

        FiiTax result = taxCalculator.calculeFiiTaxesManual(fii);
        //StaticStorage.addStock(fii.getClientId(), result);
        result.setName(result.getName().toUpperCase());


        logger.info(result.toString());
        return result;

    }

    @ApiOperation("Calculates de total taxes for FII based on PDF file without name")
    @CrossOrigin(origins = "*")
    @PostMapping(path="/fii/automatic", produces = "application/json")
    public @ResponseBody FiiTaxList calculateFiiTaxesAutomaticNoName (@RequestBody FiiDataPdf fii,
                                                            @RequestHeader("fiiz-tax-api-key") String header) throws Throwable {

        logger.info(fii.getName());

        FiiTaxList response = new FiiTaxList();
        response.setFiiTaxesList(convertToFiiTax(fii.getCorretagem(), fii.getSubscricoes()));

        return response;

    }


    @ApiOperation("Returns all IPOs and Subscricoes based on the receipt")
    @CrossOrigin(origins = "*")
    @PostMapping(path="/fii/subscricao", produces = "application/json")
    public @ResponseBody FiiSubscricaoList subscricaoList (@RequestBody FiiSubscricaoInputPdf fii,
                                                                      @RequestHeader("fiiz-tax-api-key") String header) throws Throwable {

        logger.info(String.valueOf(fii.getClient_id()));

        FiiSubscricaoList response = new FiiSubscricaoList();

        response.setFiiSubscricaoList(convertToSubscricao(fii.getSubscricaoPurchase(), fii.getCorretora()));

        return response;

    }


    @ApiOperation("Provides all FIIs for a given client")
    @CrossOrigin(origins = "*")
    @GetMapping(path="/fii/wallet/{id}", produces = "application/json")
    @Deprecated
    public @ResponseBody FiiTaxList getFiis (@PathVariable int id,
                                             @RequestHeader("fiiz-tax-api-key") String header) throws Throwable {

        logger.info("Consult id:"+id);

        FiiTaxList fiiTaxList = new FiiTaxList();
        fiiTaxList.setFiiTaxesList(StaticStorage.getStock(id));
        return fiiTaxList;
    }


}
