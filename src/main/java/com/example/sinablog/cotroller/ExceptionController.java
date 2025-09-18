package com.example.sinablog.cotroller;


import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.ExceptionDto;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {

    private final MessageSourceAccessor messageSourceAccessor;

    public ExceptionController(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @ExceptionHandler(RuleException.class)
    public ResponseEntity<List<ExceptionDto>> ruleException(RuleException e) {
        List<ExceptionDto> dtos = new ArrayList<>();
        dtos.add(new ExceptionDto(messageSourceAccessor.getMessage(e.getMessage()), e.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(dtos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List<ExceptionDto>> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        List<ExceptionDto> dtos = new ArrayList<>();
        dtos.add(new ExceptionDto(
                messageSourceAccessor.getMessage("enum.not.exist"),
                "enum.not.exist"
        ));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(dtos);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ExceptionDto>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ExceptionDto> collect = e.getBindingResult().getFieldErrors()
                .stream().map(error -> new ExceptionDto(messageSourceAccessor.getMessage(error.getDefaultMessage())
                        , error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(collect);
    }

}
