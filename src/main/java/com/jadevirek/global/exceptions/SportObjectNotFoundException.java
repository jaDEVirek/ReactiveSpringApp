package com.jadevirek.global.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SportObjectNotFoundException extends RuntimeException {

    public SportObjectNotFoundException(String message) {
        super("Sport with name %s has not found" + message);
    }
}
