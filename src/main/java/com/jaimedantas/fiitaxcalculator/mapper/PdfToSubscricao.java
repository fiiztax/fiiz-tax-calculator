package com.jaimedantas.fiitaxcalculator.mapper;

import com.jaimedantas.fiitaxcalculator.controller.RestController;
import com.jaimedantas.fiitaxcalculator.exception.FileTooBigException;
import com.jaimedantas.fiitaxcalculator.model.FiiSubscricao;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.web.Link;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PdfToSubscricao {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    private PdfToSubscricao(){}

    public static LinkedList<FiiSubscricao> convertToSubscricao(byte[] input, String corretora) throws FileTooBigException, IOException, ParseException {

        String pattern = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        LinkedList<FiiSubscricao> fiiSubscricaoList = new LinkedList<>();

        if (input.length>1048576){
            throw new FileTooBigException("PDF muito grande.");
        }

        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument inputPdf = PDDocument.load(input);

        String inputText = pdfStripper.getText(inputPdf);

        LinkedList<String> stringLineList = new LinkedList<>();

        String[] subscricaoTextCore = inputText.split("COMPRA DE OFERTA DE AÇÕES", -2);
        String[] subscricaoTextCore2 = inputText.split("SUBSCRIÇÃO", -2);


        putDataIntoList(stringLineList, subscricaoTextCore);

        putDataIntoList(stringLineList, subscricaoTextCore2);

        stringLineList.forEach(v ->{
            int k = 0;
            if (v.contains("PROV")){
                k = 3;
            }
            FiiSubscricao fiiSubscricao = new FiiSubscricao();
            String[] subscricaoClear = v.split(" ");

            try {
                Date date = simpleDateFormat.parse(subscricaoClear[0]);
                fiiSubscricao.setDate(date);
            } catch (ParseException e) {
                logger.error("Error ao converter data de subscricao", e);
            }

            String name = subscricaoClear[3+k].substring(2,6) + " - " + subscricaoClear[3+k].substring(6, subscricaoClear[3+k].length());
            fiiSubscricao.setName(name);
            fiiSubscricao.setCorretora(corretora);
            fiiSubscricao.setTransactionId(UUID.randomUUID().toString());
            fiiSubscricao.setQuantityBought(Long.parseLong(subscricaoClear[subscricaoClear.length - 3]));
            subscricaoClear[subscricaoClear.length - 2] = subscricaoClear[subscricaoClear.length - 2]
                    .replace(".", "")
                    .replace(",", ".")
                    .replace("R", "")
                    .replace("$", "")
                    .replace("-", "")
                    .replace(" ", "").trim();
            fiiSubscricao.setTotalBought(new BigDecimal(subscricaoClear[subscricaoClear.length - 2]));
            fiiSubscricaoList.add(fiiSubscricao);
        });


        return fiiSubscricaoList;
    }

    private static void putDataIntoList(LinkedList<String> stringLineList, String[] subscricaoTextCore) {
        for (int i=1; i<subscricaoTextCore.length; i++){
            String[] subscricaoTextUnitData = subscricaoTextCore[i-1].split("\\n");
            String[] subscricaoTextUnit = subscricaoTextCore[i].split("\\n");
            String valueWithDate0 = subscricaoTextUnitData[subscricaoTextUnitData.length-1] + subscricaoTextUnit[0];
            stringLineList.add(valueWithDate0);
        }
    }
}
