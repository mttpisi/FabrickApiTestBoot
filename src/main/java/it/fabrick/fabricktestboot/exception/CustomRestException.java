package it.fabrick.fabricktestboot.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//eccezione custom per errori dell'api fabrick
@Data
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomRestException extends RuntimeException{

    public CustomRestException(String msg) { super(msg); }

}
