package it.fabrick.fabricktestboot.exception;

import it.fabrick.fabricktestboot.model.proxy.BaseProxy;
import it.fabrick.fabricktestboot.model.proxy.ProxyError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    //logga in console le eccezioni (in questo caso tutte) e ritorna un oggetto BaseProxy
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<BaseProxy> genericException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(BaseProxy.builder().status("KO").payload(new ProxyError(e.getMessage())).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
