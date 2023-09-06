package org.lincoln.compras.rest.controller;

import org.lincoln.compras.exception.PedidoNotFoundException;
import org.lincoln.compras.exception.RegraNegocioException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.lincoln.compras.rest.ApiErrors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiErrors handleRegraNegocioException (RegraNegocioException ex){
        return new ApiErrors(ex.getMessage());
    }

    @ExceptionHandler(PedidoNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ApiErrors handlePedidoNotFoundException (PedidoNotFoundException ex){
        return new ApiErrors(ex.getMessage());
    }

}
