package com.example.hierarchy.service

import com.example.hierarchy.model.Employee
import com.example.hierarchy.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeService(@Autowired private val employeeRepository: EmployeeRepository) {
    fun buildEmployeeHierarchy(employeeNameToSupervisorName: Map<String, String>): Employee {
        val nameToEmployee = employeeNameToSupervisorName.entries
                .fold(mutableMapOf<String, Employee>()) { acc, entry ->
                    acc.putIfAbsent(entry.key, Employee(entry.key, null))
                    acc.putIfAbsent(entry.value, Employee(entry.value, null))

                    acc[entry.key]!!.supervisor = acc[entry.value]
                    acc[entry.value]!!.subordinates.add(acc[entry.key]!!)

                    return@fold acc
                }

        return findRootEmployee(nameToEmployee.values.toList())
    }

    @Transactional
    fun saveEmployeeHierarchy(rootEmployee: Employee): Employee {
        employeeRepository.deleteAll()
        return employeeRepository.save(rootEmployee)
    }

    fun employeesToHierarchyMap(employee: List<Employee>): Map<String, Any> {
        return employee.associateBy({ it.name }) { employeesToHierarchyMap(it.subordinates) }
    }

    private fun findRootEmployee(employees: List<Employee>): Employee {
        return employees.find { it.supervisor == null }!!
    }
}
