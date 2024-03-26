package com.bioproj.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGlobalException(Exception e, HttpServletRequest request) {
//        BaseResponse baseResponse = handleBaseException(e);
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        baseResponse.setStatus(status.value());
//        baseResponse.setMessage(e.getMessage());
////        return baseResponse;
        return mvException(e,request,HttpStatus.INTERNAL_SERVER_ERROR.value());

    }
    private  ModelAndView mvException(Throwable t,HttpServletRequest request,int status) {
        Assert.notNull(t, "Throwable must not be null");
        log.error("request.getRequestURL:{}",request.getRequestURL().toString());
        log.error("Captured an exception", t);

        ModelAndView modelAndView;
        modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("msg",t.getMessage());
        modelAndView.addObject("code",status);
        return modelAndView;
    }
}
