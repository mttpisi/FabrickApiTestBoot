package it.fabrick.fabricktestboot.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.fabricktestboot.exception.CustomRestException;
import it.fabrick.fabricktestboot.model.*;
import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.model.proxy.ListProxy;
import it.fabrick.fabricktestboot.repository.BalanceRepository;
import it.fabrick.fabricktestboot.repository.TransactionRepository;
import it.fabrick.fabricktestboot.repository.TransactionTypeRepository;
import it.fabrick.fabricktestboot.utils.FabrickApiUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FabrickServiceTest {

    @Mock
    BalanceRepository balanceRepositoryMock;

    @Mock
    TransactionRepository transactionRepositoryMock;

    @Mock
    TransactionTypeRepository transactionTypeRepository;

    @InjectMocks
    FabrickService fabrickService;

    private String accountId = "14537780";

    @BeforeAll
    static void beforeAll() {
        Mockito.mockStatic(FabrickApiUtils.class);

        when(FabrickApiUtils.setupHeaders()).thenCallRealMethod();
        when(FabrickApiUtils.getBalanceUrl(any(String.class))).thenCallRealMethod();
        when(FabrickApiUtils.getTransactionListUrl(any(String.class))).thenCallRealMethod();
        when(FabrickApiUtils.getMoneyTransferUrl(any(String.class))).thenCallRealMethod();
    }

    @Test
    @DisplayName("Test getBalance return")
    public void testGetBalance_checkReturnCorrectness() throws IOException {
        Balance balance = new Balance().builder().date("2021-01-01").balance(BigDecimal.valueOf(3.97)).availableBalance(BigDecimal.valueOf(3.97)).currency("EUR").build();
        BaseProxy res = BaseProxy.builder()
                .status("OK")
                .payload(balance).build();

        when(FabrickApiUtils.exchangeWithApi(any(String.class), any(HttpMethod.class), any(HttpEntity.class), isNull()))
                .thenReturn(res);

        BaseProxy serviceRes = fabrickService.getBalance(accountId);
        Balance balanceRes = (new ObjectMapper()).convertValue(serviceRes.getPayload(), Balance.class);
        Assertions.assertEquals(balance.getDate(), balanceRes.getDate());
        Assertions.assertEquals(balance.getBalance(), balanceRes.getBalance());
        Assertions.assertEquals(balance.getAvailableBalance(), balanceRes.getAvailableBalance());
        Assertions.assertEquals(balance.getCurrency(), balanceRes.getCurrency());
    }

    @Test
    @DisplayName("Test getTransactionList return")
    public void testGetTransactionList_checkReturnCorrectness() throws IOException {
        Transaction transaction = new Transaction().builder()
                .transactionId("111")
                .accountingDate("2021-01-01")
                .amount(BigDecimal.valueOf(10))
                .currency("EUR")
                .description("description")
                .operationId("operationId")
                .type(new TransactionType("enum", "val")).build();
        ListProxy list = new ListProxy(Collections.singletonList(transaction));

        BaseProxy res = BaseProxy.builder()
                .status("OK")
                .payload(list).build();

        when(FabrickApiUtils.exchangeWithApi(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Map.class)))
                .thenReturn(res);
        System.out.println("result" + FabrickApiUtils.exchangeWithApi("", HttpMethod.GET, new HttpEntity<>(""), new HashMap<>()));

        BaseProxy serviceRes = fabrickService.getTransactionList(accountId, "2021-01-01", "2021-12-01");
        ListProxy listProxy = (new ObjectMapper()).convertValue(res.getPayload(), ListProxy.class);
        List<Transaction> transactions = (new ObjectMapper()).convertValue(listProxy.getList(), new TypeReference<List<Transaction>>() {});
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(transaction.getTransactionId(), transactions.get(0).getTransactionId());
        Assertions.assertEquals(transaction.getAccountingDate(), transactions.get(0).getAccountingDate());
        Assertions.assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
        Assertions.assertEquals(transaction.getCurrency(), transactions.get(0).getCurrency());
        Assertions.assertEquals(transaction.getDescription(), transactions.get(0).getDescription());
        Assertions.assertEquals(transaction.getOperationId(), transactions.get(0).getOperationId());
        Assertions.assertNotNull(transactions.get(0).getType());
        Assertions.assertEquals(transaction.getType().getEnumeration(), transactions.get(0).getType().getEnumeration());
        Assertions.assertEquals(transaction.getType().getValue(), transactions.get(0).getType().getValue());
    }

    @Test
    @DisplayName("Test createMoneyTransfer exception")
    public void testCreateMoneyTransfers_checkThrowException() throws IOException {
        MoneyTransfer moneyTransfer = new MoneyTransfer().builder()
                .creditor(new Creditor("nome", new CreditorAccount("accountCode")))
                .amount(BigDecimal.valueOf(10))
                .currency("EUR")
                .description("description")
                .executionDate("2021-01-01").build();

        CustomRestException exception = new CustomRestException("errore api");
        when(FabrickApiUtils.exchangeWithApi(any(String.class), any(HttpMethod.class), any(HttpEntity.class), isNull()))
                .thenThrow(exception);

        Exception exceptionRes = Assertions.assertThrows(CustomRestException.class, () -> {
            fabrickService.createMoneyTransfer(accountId, moneyTransfer);
        });

        Assertions.assertTrue(exception.getMessage().contains(exceptionRes.getMessage()));
    }

}
