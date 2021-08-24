package com.jaimedantas.fiitaxcalculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaimedantas.fiitaxcalculator.model.FiiData;
import com.jaimedantas.fiitaxcalculator.model.FiiDataPdf;
import com.jaimedantas.fiitaxcalculator.model.FiiSubscricaoInputPdf;
import org.apache.pdfbox.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RestController.class)
public class RestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shoudCallApiAndReturnSucess() throws Exception {

        FiiData imput = new FiiData();
        imput.setTotalValueSold(new BigDecimal("9029"));
        imput.setTotalValueBought(new BigDecimal("1029"));
        imput.setClientId(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(imput);

        this.mvc.perform(post("/tax/fii/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shoudCallApiAndReturnSucessInGetAndPost() throws Exception {

        FiiData imput = new FiiData();
        imput.setTotalValueSold(new BigDecimal("9029"));
        imput.setTotalValueBought(new BigDecimal("1029"));
        imput.setClientId(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(imput);

        this.mvc.perform(post("/tax/fii/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());

        this.mvc.perform(get("/tax/fii/wallet/{id}", 1L)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c"))
                .andExpect(status().isOk());

    }

/*

    @Test
    public void shoudCallApiPdfAndReturnSucess() throws Exception {

        //VALOR BOSSULA: 616,55
        //VALOR JAIME: 616,27

        ClassLoader classLoader = getClass().getClassLoader();

        File filePurchase = new File(classLoader.getResource("PATRIA_COMPRA.pdf").getFile());
        File fileSold = new File(classLoader.getResource("PATRIA_VENDA.pdf").getFile());

        FileInputStream inputP = new FileInputStream(filePurchase);
        FileInputStream inputS = new FileInputStream(fileSold);


        MultipartFile resultP = new MockMultipartFile("PATRIA_COMPRA.pdf",
                "PATRIA_COMPRA.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputP));

        MultipartFile resultS = new MockMultipartFile("PATRIA_VENDA.pdf",
                "PATRIA_VENDA.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputS));


        byte[] documentPurchase = readFileToByteArray(filePurchase);
        byte[] documentSold = readFileToByteArray(fileSold);

        FiiDataPdf fiiDataPdf = new FiiDataPdf();
        fiiDataPdf.setCorretagemPurchase(resultP.getBytes());
        fiiDataPdf.setCorretagemSold(resultS.getBytes());
        fiiDataPdf.setName("PATC");
        fiiDataPdf.setClient_id(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fiiDataPdf);


        this.mvc.perform(post("/tax/fii/automatic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }



    @Test
    public void shoudCallApiPdfAndReturnSucessFiiAndStock() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        File filePurchase = new File(classLoader.getResource("ACAO_AND_FII.pdf").getFile());
        File fileSold = new File(classLoader.getResource("PATRIA_VENDA.pdf").getFile());

        FileInputStream inputP = new FileInputStream(filePurchase);
        FileInputStream inputS = new FileInputStream(fileSold);


        MultipartFile resultP = new MockMultipartFile("TODO_MES_DE_JANEIRO.pdf",
                "ACAO_AND_FII.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputP));

        MultipartFile resultS = new MockMultipartFile("PATRIA_VENDA.pdf",
                "PATRIA_VENDA.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputS));
        String k = "";

        byte[] documentPurchase = readFileToByteArray(filePurchase);
        byte[] documentSold = readFileToByteArray(fileSold);

        FiiDataPdf fiiDataPdf = new FiiDataPdf();
        fiiDataPdf.setCorretagemPurchase(resultP.getBytes());
        fiiDataPdf.setCorretagemSold(k.getBytes());
        fiiDataPdf.setName("");
        fiiDataPdf.setClient_id(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fiiDataPdf);


        this.mvc.perform(post("/tax/fii/automatic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void shoudCallApiPdfAndReturnSucessNoName() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        File filePurchase = new File(classLoader.getResource("TODO_MES_DE_JANEIRO.pdf").getFile());
        File fileSold = new File(classLoader.getResource("PATRIA_VENDA.pdf").getFile());

        FileInputStream inputP = new FileInputStream(filePurchase);
        FileInputStream inputS = new FileInputStream(fileSold);


        MultipartFile resultP = new MockMultipartFile("TODO_MES_DE_JANEIRO.pdf",
                "TODO_MES_DE_JANEIRO.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputP));

        MultipartFile resultS = new MockMultipartFile("PATRIA_VENDA.pdf",
                "PATRIA_VENDA.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputS));

        String k = "";

        byte[] documentPurchase = readFileToByteArray(filePurchase);
        byte[] documentSold = readFileToByteArray(fileSold);

        FiiDataPdf fiiDataPdf = new FiiDataPdf();
        fiiDataPdf.setCorretagemPurchase(resultP.getBytes());
        fiiDataPdf.setCorretagemSold(k.getBytes());
        fiiDataPdf.setName("");
        fiiDataPdf.setClient_id(1);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fiiDataPdf);


        this.mvc.perform(post("/tax/fii/automatic")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void shoudCallSubscricaoXp() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        File filePurchase = new File(classLoader.getResource("Extrato_XP_6_MESES_printer.pdf").getFile());

        FileInputStream inputP = new FileInputStream(filePurchase);


        MultipartFile resultP = new MockMultipartFile("Extrato_XP_6_MESES_printer.pdf",
                "Extrato_XP_6_MESES.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputP));


        byte[] documentPurchase = readFileToByteArray(filePurchase);

        FiiSubscricaoInputPdf fiiSubscricaoInputPdf = new FiiSubscricaoInputPdf();
        fiiSubscricaoInputPdf.setClient_id(1);
        fiiSubscricaoInputPdf.setCorretora("XP");
        fiiSubscricaoInputPdf.setSubscricaoPurchase(resultP.getBytes());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fiiSubscricaoInputPdf);


        this.mvc.perform(post("/tax/fii/subscricao")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shoudCallSubscricaoXp2() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        File filePurchase = new File(classLoader.getResource("Extrato_XP_MES.pdf").getFile());

        FileInputStream inputP = new FileInputStream(filePurchase);


        MultipartFile resultP = new MockMultipartFile("Extrato_XP_MES.pdf",
                "Extrato_XP_MES.pdf",
                "text/pdf",
                IOUtils.toByteArray(inputP));


        byte[] documentPurchase = readFileToByteArray(filePurchase);

        FiiSubscricaoInputPdf fiiSubscricaoInputPdf = new FiiSubscricaoInputPdf();
        fiiSubscricaoInputPdf.setClient_id(1);
        fiiSubscricaoInputPdf.setCorretora("XP");
        fiiSubscricaoInputPdf.setSubscricaoPurchase(resultP.getBytes());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fiiSubscricaoInputPdf);


        this.mvc.perform(post("/tax/fii/subscricao")
                .contentType(MediaType.APPLICATION_JSON)
                .header("fiiz-tax-api-key", "21dead60-cef2-40b7-8864-7990e5563a6c")
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }

*/


    /**
     * This method uses java.io.FileInputStream to read
     * file content into a byte array
     * @param file
     * @return
     */
    private static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }


}
