package com.example.hierarchy.controller

import com.example.hierarchy.dto.Employee
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EmployeeController {
    @GetMapping("/employee-hierarchy")
    fun employeeHierarchy() = Employee("Hello world")
}
