package de.iteratec.iteraOfficeMap.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Entry does not exists")
public class DoesNotExistException extends RuntimeException {

}
