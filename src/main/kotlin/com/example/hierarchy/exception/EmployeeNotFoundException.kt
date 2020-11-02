package com.example.hierarchy.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = NOT_FOUND)
class EmployeeNotFoundException(message: String) : RuntimeException(message)
