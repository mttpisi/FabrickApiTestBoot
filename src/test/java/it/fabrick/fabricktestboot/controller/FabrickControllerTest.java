package it.fabrick.fabricktestboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.fabricktestboot.exception.CustomRestException;
import it.fabrick.fabricktestboot.model.*;
import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.service.FabrickService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FabrickController.class)
public class FabrickControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    FabrickService fabrickServiceMock;

    @InjectMocks
    FabrickController fabrickController;

    private String accountId = "14537780";

    @Test
    @DisplayName("Test Debug")
    public void testDebug() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/fabrick/debug"))
                .andExpect(status().isOk())
                .andExpect(content().string("debug"));
    }

    @Test
    @DisplayName("Test getBalance success")
    public void testGetBalance_checkResponseCorrectness_VerifyMethod() throws Exception {
        BaseProxy res = BaseProxy.builder()
                .status("OK")
                .payload(new Balance().builder().date("2021-01-01").balance(BigDecimal.valueOf(3.97)).availableBalance(BigDecimal.valueOf(3.97)).currency("EUR").build()).build();

        when(fabrickServiceMock.getBalance(accountId)).thenReturn(res);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/fabrick/account/" + accountId + "/balance").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.payload.date").value("2021-01-01"))
                .andExpect(jsonPath("$.payload.balance").value(3.97))
                .andExpect(jsonPath("$.payload.availableBalance").value(3.97))
                .andExpect(jsonPath("$.payload.currency").value("EUR"));

        verify(fabrickServiceMock).getBalance(accountId);
    }

    @Test
    @DisplayName("Test getTransactionList success")
    public void testGetTransactionList_checkResponseCorrectness_VerifyMethod() throws Exception {
        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction().builder()
                .transactionId("111")
                .accountingDate("2021-01-01")
                .amount(BigDecimal.valueOf(10))
                .currency("EUR")
                .description("description")
                .operationId("operationId")
                .type(new TransactionType("enum", "val")).build());

        BaseProxy res = BaseProxy.builder()
                .status("OK")
                .payload(list).build();

        when(fabrickServiceMock.getTransactionList(accountId, "2021-01-01", "2021-01-01")).thenReturn(res);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/fabrick/account/" + accountId + "/transaction/list").param("fromAccountingDate", "2021-01-01").param("toAccountingDate", "2021-01-01").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload[0].transactionId").value("111"))
                .andExpect(jsonPath("$.payload[0].accountingDate").value("2021-01-01"))
                .andExpect(jsonPath("$.payload[0].amount").value(10))
                .andExpect(jsonPath("$.payload[0].currency").value("EUR"))
                .andExpect(jsonPath("$.payload[0].description").value("description"))
                .andExpect(jsonPath("$.payload[0].operationId").value("operationId"))
                .andExpect(jsonPath("$.payload[0].type.enumeration").value("enum"))
                .andExpect(jsonPath("$.payload[0].type.value").value("val"));

        verify(fabrickServiceMock).getTransactionList(accountId, "2021-01-01", "2021-01-01");
    }

    @Test
    @DisplayName("Test createMoneyTransfer exception")
    public void testCreateMoneyTransfer_checkResponseException_VerifyMethod() throws Exception {
        MoneyTransfer transfer = new MoneyTransfer().builder()
                .creditor(new Creditor("nome", new CreditorAccount("accountCode")))
                .amount(BigDecimal.valueOf(10))
                .currency("EUR")
                .description("description")
                .executionDate("2021-01-01").build();

        String body = (new ObjectMapper()).writeValueAsString(transfer);

        when(fabrickServiceMock.createMoneyTransfer(anyString(), any(MoneyTransfer.class))).thenThrow(new CustomRestException("errore api"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/fabrick/account/" + accountId + "/moneytransfer/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("KO"))
                .andExpect(jsonPath("$.payload.description").isString());

        verify(fabrickServiceMock).createMoneyTransfer(anyString(), any(MoneyTransfer.class));
    }

}
