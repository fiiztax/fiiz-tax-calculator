package com.jaimedantas.fiitaxcalculator.mapper;

import com.jaimedantas.fiitaxcalculator.exception.FileTooBigException;
import com.jaimedantas.fiitaxcalculator.model.AllTransactionsFromPdf;
import com.jaimedantas.fiitaxcalculator.model.IndividualTransaction;
import com.jaimedantas.fiitaxcalculator.ultils.CalculationEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PdfToFiiDataMonth {

    final static String SALE = "V";
    final static String BUY = "C";
    final static String STOCK = "STOCK";
    final static String FII = "FII";

    private PdfToFiiDataMonth(){}

    public static AllTransactionsFromPdf convertToFiiData(byte[] input) throws IOException, FileTooBigException, ParseException {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (input.length>1048576){
            throw new FileTooBigException("PDF muito grande.");
        }

        String corretagem1Text;

        try (PDDocument corretagem1Pdf = PDDocument.load(input)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            corretagem1Text = pdfStripper.getText(corretagem1Pdf);
        }


        String[] corretagem1TextCore = corretagem1Text.split("NOTA DE CORRETAGEM", -2);

        String[] corretagemAllDates = corretagem1Text.split("Data pregão", -2);
        LinkedList<String> datesListString = new LinkedList<>();
        Arrays.stream(corretagemAllDates).forEach( v -> {
            datesListString.add(v.substring(1,11));
        });
        datesListString.removeFirst();//garbage
        LinkedList<Date> datesList = new LinkedList<>();
        datesListString.forEach( v -> {
            try {
                Date date = simpleDateFormat.parse(v);
                datesList.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


        String[] corretagem1ByProduct = corretagem1TextCore[0].split("1-BOVESPA", -2);

        if (corretagem1ByProduct[corretagem1ByProduct.length-1].contains("NOTA DE NEGOCIA")){
            String parts[] =
                    corretagem1ByProduct[corretagem1ByProduct.length-1].split("NOTA DE NEGOCIA");
            corretagem1ByProduct[corretagem1ByProduct.length-1] = parts[0];
        }

        LinkedList<IndividualTransaction> extractProductFromText1 = extractProductFromText(corretagem1ByProduct, datesList);

        Set<String> set = new HashSet<>(extractProductFromText1.size());
        extractProductFromText1.stream().filter(p -> set.add(p.getName())).collect(Collectors.toList());
        List<String> list = new ArrayList<>(set);


        //for all purchases
        final LinkedList<IndividualTransaction> finalTransactionsPurchase = initList(extractProductFromText1, list, BUY, datesList);

        //for all sales
        final LinkedList<IndividualTransaction> finalTransactionsSale = initList(extractProductFromText1, list, SALE, datesList);

        AllTransactionsFromPdf allTransactionsFromPdf = new AllTransactionsFromPdf();

        allTransactionsFromPdf.setFinalTransactionsPurchase(finalTransactionsPurchase);
        allTransactionsFromPdf.setFinalTransactionsSale(finalTransactionsSale);



        return allTransactionsFromPdf;
    }

    private static LinkedList<IndividualTransaction> initList(LinkedList<IndividualTransaction> extractProductFromTextPurchase,
                                                              List<String> list, String saleType, LinkedList<Date> datesList) {

        LinkedList<IndividualTransaction> response = new LinkedList<>();

        datesList.forEach(d -> {
            list.forEach((v)->{
                IndividualTransaction individualTransaction = new IndividualTransaction();
                individualTransaction.setName(v);
                individualTransaction.setTransactionType(saleType);
                individualTransaction.setProductType(FII);
                individualTransaction.setDate(d);

                Optional<BigDecimal> totalPurchase = extractProductFromTextPurchase.stream()
                        .filter(x -> x.getTransactionType().equals(saleType))
                        .filter(x -> x.getName().equals(v))
                        .filter(x -> x.getProductType().equals(FII))
                        .filter(x -> x.getDate().equals(d))
                        .map(x -> x.getTotalValue())
                        .reduce(CalculationEngine::add);

                Optional<Long> totalPurchaseUnit = extractProductFromTextPurchase.stream()
                        .filter(x -> x.getTransactionType().equals(saleType))
                        .filter(x -> x.getName().equals(v))
                        .filter(x -> x.getProductType().equals(FII))
                        .filter(x -> x.getDate().equals(d))
                        .map(x -> x.getTotalUnit())
                        .reduce(Long::sum);

                try {
                    individualTransaction.setTotalUnit(totalPurchaseUnit.get());
                    individualTransaction.setTotalValue(totalPurchase.get());
                    response.add(individualTransaction);
                } catch (Exception e){
                    //do nothing
                }
            });
        });


        return response;
    }

    private static LinkedList<IndividualTransaction> extractProductFromText(String[] products, LinkedList<Date>  datesList) throws ParseException {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        LinkedList<IndividualTransaction> listOfTransactions = new LinkedList<>();
        int incrementDate = 0;

        for (int i = 1; i< products.length; i++) {
            IndividualTransaction individualTransaction = new IndividualTransaction();
            String transactionType = products[i].substring(0, 2).trim();
            String names[] = products[i].split("          ");
            //String total[] = names[2].split("\\n");
            String total[] = names[names.length-1].split("\\n");
            String totalClear[] = total[0].split(" ");
            totalClear[totalClear.length - 2] = totalClear[totalClear.length - 2].replace(".", "");
            totalClear[totalClear.length - 2] = totalClear[totalClear.length - 2].replace(",", ".");
            BigDecimal totalBigdecimal = new BigDecimal(totalClear[totalClear.length - 2]);
            Long totalUnitLong = Long.valueOf(totalClear[totalClear.length - 4]);

            individualTransaction.setName(names[1].substring(0, 4));
            individualTransaction.setTotalValue(totalBigdecimal);
            individualTransaction.setTransactionType(transactionType);
            individualTransaction.setTotalUnit(totalUnitLong);
            if (names[0].contains("FII")){
                individualTransaction.setProductType(FII);
            } else {
                individualTransaction.setProductType(STOCK);
            }

            individualTransaction.setDate(datesList.get(incrementDate));
            listOfTransactions.add(individualTransaction);
            if (products[i].contains("NOTA DE NEGOCIA")){
                incrementDate++;
            }
        }
        return listOfTransactions;
    }

}





