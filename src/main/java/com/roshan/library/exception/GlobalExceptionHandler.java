package com.roshan.library.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Turns exceptions that escape a controller into a readable error page.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return errorPage("Record not found", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleIntegrity(DataIntegrityViolationException ex) {
        return errorPage("Data integrity violation",
                "The change could not be saved because it conflicts with existing data.");
    }

    private ModelAndView errorPage(String title, String message) {
        ModelAndView mv = new ModelAndView("common/error");
        mv.addObject("errorTitle", title);
        mv.addObject("errorMessage", message);
        return mv;
    }
}
