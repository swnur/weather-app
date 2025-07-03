package com.swnur.advice;

import com.swnur.exception.InvalidCredentialsException;
import com.swnur.exception.LoginAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleLoginAlreadyExists(LoginAlreadyExistsException ex) {
        ModelAndView modelAndView = new ModelAndView("auth/sign-up");
        modelAndView.addObject("error", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ModelAndView modelAndView = new ModelAndView("auth/sign-in");
        modelAndView.addObject("error", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error/general-error");
        modelAndView.addObject("errorMessage", ex.getMessage());

        ex.printStackTrace(); // todo: replace (for debugging only)
        return modelAndView;
    }
}
