package com.example.hierarchy.repository

import com.example.hierarchy.model.Employee
import com.example.hierarchy.projection.EmployeeProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EmployeeRepository : JpaRepository<Employee, Long> {
    @Query("""
        SELECT e1.name as name,
               e2.name as supervisor,
               e3.name as supervisorsSupervisor
        FROM Employee as e1
                 LEFT JOIN e1.supervisor as e2
                 LEFT JOIN e2.supervisor as e3
        WHERE e1.name = :name
    """)
    fun getEmployeeSuperiors(name: String): EmployeeProjection?
}
