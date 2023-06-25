package it.fabrick.fabricktestboot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.fabricktestboot.exception.CustomRestException;
import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.model.proxy.ProxyError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class FabrickApiUtils {

    private static final String baseUrl = "https://sandbox.platfr.io";

    private static final String accountingEndpoint = "/api/gbs/banking/v4.0/accounts";

    private static final String apiKey = "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP";

    private static final String authSchema = "S2S";

    public static HttpHeaders setupHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Auth-Schema", authSchema);
        headers.set("Api-Key", apiKey);
        return headers;
    }

    public static String getTransactionListUrl(String accountId) {
        return baseUrl + accountingEndpoint + "/" + accountId + "/transactions?fromAccountingDate={fromAccountingDate}&toAccountingDate={toAccountingDate}";
    }
    public static String getMoneyTransferUrl(String accountId) {
        return baseUrl + accountingEndpoint + "/" + accountId + "/payments/money-transfers";
    }

    public static String getBalanceUrl(String accountId) {
        return baseUrl + accountingEndpoint + "/" + accountId + "/balance";
    }

    //Metodo di interrogazione api con gestione di eventuali errori HTTP e lancio CustomRestException
    public static BaseProxy exchangeWithApi(String url, HttpMethod httpMethod, HttpEntity<String> httpEntity, Map<String, Object> params) throws IOException {
        RestTemplate t = new RestTemplate();
        BaseProxy res = null;
        log.info("Launching " + httpMethod.toString() + " request to " + url);
        try {
            ResponseEntity<BaseProxy> response = t.exchange(url, httpMethod, httpEntity, BaseProxy.class, params != null ? params : new HashMap<>());
            res = response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getMessage() != null) {
                res = (new ObjectMapper()).readValue(e.getResponseBodyAsString(), BaseProxy.class);
                Optional<ProxyError> error = res.getErrors().stream().findFirst();
                if (error.isPresent()) {
                    throw new CustomRestException(error.get().getCode() + " - " + error.get().getDescription());
                }
            }
            throw new CustomRestException(e.getMessage());
        }
        return res;
    }
}
