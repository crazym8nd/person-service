package com.bnm.personservice.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Void> handleRuntimeException(final RuntimeException ex) {
    if (ex.getMessage().contains("not found")) {
      return ResponseEntity.notFound().build();
    }
    throw ex;
  }
} 