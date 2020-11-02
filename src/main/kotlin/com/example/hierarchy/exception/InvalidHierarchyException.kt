package com.example.hierarchy.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = BAD_REQUEST)
class InvalidHierarchyException(message: String) : RuntimeException(message)
