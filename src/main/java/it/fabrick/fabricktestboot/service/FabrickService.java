package it.fabrick.fabricktestboot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.fabrick.fabricktestboot.model.Balance;
import it.fabrick.fabricktestboot.model.MoneyTransfer;
import it.fabrick.fabricktestboot.model.Transaction;
import it.fabrick.fabricktestboot.model.TransactionType;
import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.model.proxy.ListProxy;
import it.fabrick.fabricktestboot.repository.BalanceRepository;
import it.fabrick.fabricktestboot.repository.TransactionRepository;
import it.fabrick.fabricktestboot.repository.TransactionTypeRepository;
import it.fabrick.fabricktestboot.utils.FabrickApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FabrickService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    //metodo di recupero e salvataggio di un bilancio dato un accountId
    public BaseProxy getBalance(String accountId) throws IOException {
        HttpEntity<String> httpEntity = new HttpEntity<>(FabrickApiUtils.setupHeaders());
        BaseProxy res = FabrickApiUtils.exchangeWithApi(FabrickApiUtils.getBalanceUrl(accountId), HttpMethod.GET, httpEntity, null);
        Balance balance = (new ObjectMapper()).convertValue(res.getPayload(), Balance.class);
        balance.setAccountId(accountId);
        saveBalance(balance);
        return res;
    }

    //Creazione/Aggiornamento bilancio proveniente da api
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveBalance(Balance balance) {
        Balance toSave = balanceRepository.findByAccountId(balance.getAccountId()).orElse(balance);
        if (toSave.getId() != 0) {
            toSave.setBalance(balance.getBalance());
            toSave.setDate(balance.getDate());
            toSave.setAvailableBalance(balance.getBalance());
            toSave.setCurrency(balance.getCurrency());
            toSave.setAccountId(balance.getAccountId());
        }
        balanceRepository.saveAndFlush(toSave);
    }

    //metodo di recupero e salvataggio di una lista di transazioni dato un accountId
    public BaseProxy getTransactionList(String accountId, String fromAccountingDate, String toAccountingDate) throws IOException {
        HttpEntity<String> httpEntity = new HttpEntity<>(FabrickApiUtils.setupHeaders());
        Map<String, Object> params = new HashMap<>();
        params.put("fromAccountingDate", fromAccountingDate);
        params.put("toAccountingDate", toAccountingDate);
        BaseProxy res = FabrickApiUtils.exchangeWithApi(FabrickApiUtils.getTransactionListUrl(accountId), HttpMethod.GET, httpEntity, params);
        ListProxy listProxy = (new ObjectMapper()).convertValue(res.getPayload(), ListProxy.class);
        List<Transaction> transactions = (new ObjectMapper()).convertValue(listProxy.getList(), new TypeReference<List<Transaction>>() {});
        saveTransactions(transactions);
        return res;
    }

    //Creazione/Aggiornamento transazioni proveniente da api
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            Transaction toSave = transactionRepository.findByTransactionId(transaction.getTransactionId()).orElse(transaction);
            if (toSave.getId() != 0) {
                toSave.setTransactionId(transaction.getTransactionId());
                toSave.setOperationId(transaction.getOperationId());
                toSave.setAccountingDate(transaction.getAccountingDate());
                toSave.setValueDate(transaction.getValueDate());
                toSave.setAmount(transaction.getAmount());
                toSave.setCurrency(transaction.getCurrency());
                toSave.setDescription(transaction.getDescription());
            }
            if (toSave.getType() != null) {
                TransactionType type = transactionTypeRepository.findByEnumeration(toSave.getType().getEnumeration()).orElse(toSave.getType());
                if (type.getId() == 0) {
                    transactionTypeRepository.saveAndFlush(type);
                } else {
                    toSave.setType(type);
                }
            }
            transactionRepository.saveAndFlush(toSave);
        }
    }

    //Creazione di un bonifico dato un accountId e oggetto MoneyTransfer (corpo della richiesta http)
    public BaseProxy createMoneyTransfer(String accountId, MoneyTransfer moneyTransfer) throws IOException {
        HttpHeaders headers = FabrickApiUtils.setupHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = new ObjectMapper().writeValueAsString(moneyTransfer);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        return FabrickApiUtils.exchangeWithApi(FabrickApiUtils.getMoneyTransferUrl(accountId), HttpMethod.POST, httpEntity, null);
    }
}
