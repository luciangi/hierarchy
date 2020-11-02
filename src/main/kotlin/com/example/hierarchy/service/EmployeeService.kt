package com.example.hierarchy.service

import com.example.hierarchy.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EmployeeService(@Autowired private val employeeRepository: EmployeeRepository)
