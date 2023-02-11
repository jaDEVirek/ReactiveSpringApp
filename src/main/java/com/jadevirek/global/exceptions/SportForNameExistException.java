package com.jadevirek.global.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SportForNameExistException extends RuntimeException {
    public SportForNameExistException(String name) {
        super("Sport with name %s already exists" + name);
    }
}
