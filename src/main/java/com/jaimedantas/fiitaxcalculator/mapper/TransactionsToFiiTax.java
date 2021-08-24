package com.jaimedantas.fiitaxcalculator.mapper;

import com.jaimedantas.fiitaxcalculator.business.TaxCalculator;
import com.jaimedantas.fiitaxcalculator.exception.FileTooBigException;
import com.jaimedantas.fiitaxcalculator.model.*;
import com.jaimedantas.fiitaxcalculator.ultils.CalculationEngine;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jaimedantas.fiitaxcalculator.mapper.PdfToFiiDataMonth.BUY;
import static com.jaimedantas.fiitaxcalculator.mapper.PdfToFiiDataMonth.FII;

public class TransactionsToFiiTax{

    private TransactionsToFiiTax() {}

    public static LinkedList<FiiTax> convertToFiiTax(List<byte[]> input, List<FiiSubscricao> fiiSubscricaoList) throws IOException, FileTooBigException, ParseException {

        TaxCalculator taxCalculator = new TaxCalculator();
        LinkedList<FiiData> fiiDataList = new LinkedList<>();


        List<IndividualTransaction> allIndividualTransactionPGlobal = new ArrayList<>();
        List<IndividualTransaction> allIndividualTransactionSGlobal = new ArrayList<>();
        for (int k=0; k<input.size(); k++){
            AllTransactionsFromPdf allTransactionsFromPdf = PdfToFiiDataMonth.convertToFiiData(input.get(k));
            List<IndividualTransaction> allIndividualTransactionP = new ArrayList<>(allTransactionsFromPdf.getFinalTransactionsPurchase());
            List<IndividualTransaction> allIndividualTransactionS = new ArrayList<>(allTransactionsFromPdf.getFinalTransactionsSale());
            allIndividualTransactionPGlobal.addAll(allIndividualTransactionP);
            allIndividualTransactionSGlobal.addAll(allIndividualTransactionS);
        }

        if (!Objects.isNull(fiiSubscricaoList )){
            fiiSubscricaoList.forEach( sub ->{
                IndividualTransaction individualTransaction = new IndividualTransaction();
                individualTransaction.setFromSubscricao(true);
                individualTransaction.setName(sub.getName().substring(0,4));
                individualTransaction.setProductType(FII);
                individualTransaction.setTransactionType(BUY);
                individualTransaction.setDate(sub.getDate());
                individualTransaction.setTotalUnit(sub.getQuantityBought());
                individualTransaction.setTotalValue(sub.getTotalBought());
                allIndividualTransactionPGlobal.add(individualTransaction);
            });
        }


        findTransactions(fiiDataList, allIndividualTransactionPGlobal, allIndividualTransactionSGlobal);

        LinkedList<FiiTax>  fiiTaxList = new LinkedList<>();

        fiiDataList.forEach(v -> {
            FiiTax result = taxCalculator.calculeFiiTaxes(v);
            fiiTaxList.add(result);
        });

        return fiiTaxList;

    }

    private static void findTransactions(LinkedList<FiiData> fiiDataList, List<IndividualTransaction> allIndividualTransactionP, List<IndividualTransaction> allIndividualTransactionS) {


        LinkedList<String> listDateByMonth = new LinkedList<>();
        allIndividualTransactionS.forEach( s ->{
            if (!listDateByMonth.contains(convertToMonth(s.getDate().getTime()))){
                listDateByMonth.add(convertToMonth(s.getDate().getTime()));
            }
        });


        //this is where the math is done
        listDateByMonth.forEach(m -> {
            allIndividualTransactionS.forEach(s->{
                allIndividualTransactionP.forEach(p->{
                    if (s.getName().equals(p.getName()) && m.equals(convertToMonth(s.getDate().getTime()))) {//all sold in this month
                        //we have to search the entire list to find all p <= s_date
                        Optional<BigDecimal> totalPurchaseP = allIndividualTransactionP.stream()
                                .filter(x -> x.getTransactionType().equals(BUY))
                                .filter(x -> x.getName().equals(s.getName()))
                                .filter(x -> x.getProductType().equals(FII))
                                .filter(x -> x.getDate().getTime() < s.getDate().getTime())
                                .map(x -> x.getTotalValue())
                                .reduce(CalculationEngine::add);

                        Optional<Long> totalPurchaseUnitP = allIndividualTransactionP.stream()
                                .filter(x -> x.getTransactionType().equals(BUY))
                                .filter(x -> x.getName().equals(s.getName()))
                                .filter(x -> x.getProductType().equals(FII))
                                .filter(x -> x.getDate().getTime() <= s.getDate().getTime())
                                .map(x -> x.getTotalUnit())
                                .reduce(Long::sum);


                        FiiData fiiData1 = new FiiData();
                        fiiData1.setName(s.getName());
                        fiiData1.setTotalValueSold(s.getTotalValue());
                        fiiData1.setTotalValueBought(totalPurchaseP.get());
                        fiiData1.setQuantitySold(s.getTotalUnit());
                        fiiData1.setQuantityBought(totalPurchaseUnitP.get());
                        fiiData1.setDate(s.getDate());
                        fiiDataList.add(fiiData1);
                        s.setName("already_computed");
                    }
                });
            });
        });
    }

    /**
     * This function returns the MM/YYYY for a given time
     * @param time from date
     * @return date in MM/YYYY
     */
    private static String convertToMonth(long time){
        Date date = new Date(time);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month + "/" + year;
    }
}
