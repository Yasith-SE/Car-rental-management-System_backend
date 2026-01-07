package edu.icet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class AllExeceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class )
    public ResponseEntity <Map<String,String>>  handleError(MethodArgumentNotValidException e){
        Map<String,String> errorMaps = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errorMaps.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errorMaps, HttpStatus.BAD_REQUEST);

    }


}
