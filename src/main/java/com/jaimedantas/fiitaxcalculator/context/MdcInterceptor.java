package com.jaimedantas.fiitaxcalculator.context;


import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class MdcInterceptor extends HandlerInterceptorAdapter {

    @Value("${fiix-tax-api-key}")
    String apiKey;

    final static String FIIZ_TAX_API_KEY = "fiiz-tax-api-key";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            if (!request.getHeader(FIIZ_TAX_API_KEY).equals("21dead60-cef2-40b7-8864-7990e5563a6c")) {
                throw new UnsupportedOperationException();
            }
        } catch(Exception e){
                throw new UnsupportedOperationException();
        }
        MDC.put("CorrelationId", getCorrelationId());
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove("CorrelationId");
    }
    private String getCorrelationId() {
        return UUID.randomUUID().toString();

    }

}
