package de.iteratec.iteraOfficeMap.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Reservation is Invalid")
public class InvalidReservationException extends RuntimeException {

}
