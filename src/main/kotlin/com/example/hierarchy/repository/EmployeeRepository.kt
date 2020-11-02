package com.example.hierarchy.repository

import com.example.hierarchy.model.Employee
import org.springframework.data.repository.CrudRepository

interface EmployeeRepository : CrudRepository<Employee, Long>
