package de.iteratec.iteraOfficeMap.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Dates are invalid")
public class InvalidDatesException extends RuntimeException {

}
