package com.example.hierarchy.repository

import com.example.hierarchy.projection.EmployeeProjection
import com.example.hierarchy.model.Employee
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface EmployeeRepository : CrudRepository<Employee, Long> {
    @Query("""
        SELECT e1.name as name,
               e2.name as supervisor,
               e3.name as supervisorsSupervisor
        FROM Employee as e1
                 JOIN e1.supervisor as e2
                 JOIN e2.supervisor as e3
        WHERE e1.name = :name
    """)
    fun getSupervisors(name: String): EmployeeProjection
}
