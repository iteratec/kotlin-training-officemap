package de.iteratec.officemap.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "Entry does not exists")
class DoesNotExistException : RuntimeException()
