package it.fabrick.fabricktestboot.controller;

import it.fabrick.fabricktestboot.model.MoneyTransfer;
import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.service.FabrickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/fabrick")
public class FabrickController {

    @Autowired
    FabrickService fabrickService;

    //endpoint per recupero bilancio
    @RequestMapping(value = "/account/{accountId}/balance", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<BaseProxy> getAccount(
            @PathVariable String accountId
    ) throws IOException {
        BaseProxy res = fabrickService.getBalance(accountId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //endpoint per recupero della lista di transazioni
    @RequestMapping(value = "/account/{accountId}/transaction/list", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<BaseProxy> getTransactionList(
            @PathVariable String accountId,
            @RequestParam(value = "fromAccountingDate", required = false) String fromAccountingDate,
            @RequestParam(value = "toAccountingDate", required = false) String toAccountingDate
    ) throws IOException {
        BaseProxy res = fabrickService.getTransactionList(accountId, fromAccountingDate, toAccountingDate);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //endpoint per la creazione di un nuovo bonifico
    @RequestMapping(value = "/account/{accountId}/moneytransfer/create", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<BaseProxy> createMoneyTransfer(
            @PathVariable String accountId,
            @RequestBody MoneyTransfer moneyTransfer
    ) throws IOException {
        BaseProxy res = fabrickService.createMoneyTransfer(accountId, moneyTransfer);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> debug() {
        return new ResponseEntity<>("debug", HttpStatus.OK);
    }
}
