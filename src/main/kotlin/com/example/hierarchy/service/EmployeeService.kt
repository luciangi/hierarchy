package com.example.hierarchy.service

import com.example.hierarchy.exception.InvalidHierarchyException
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

        val employeesWithoutSupervisor = nameToEmployee.values.toList().filter { it.supervisor == null }

        if (employeesWithoutSupervisor.size > 1) {
            throw InvalidHierarchyException("Multiple roots were found. The following employees " +
                    "${employeesWithoutSupervisor.map { it.name }} do not have a supervisor")
        }
        if (employeesWithoutSupervisor.isEmpty()) {
            throw InvalidHierarchyException("The hierarchy contains a loop. No roots were found. " +
                    "At least one employee should not have a supervisor.")
        }

        return employeesWithoutSupervisor.first()
    }

    @Transactional
    fun saveEmployeeHierarchy(rootEmployee: Employee): Employee {
        employeeRepository.deleteAll()
        return employeeRepository.save(rootEmployee)
    }

    fun employeesToHierarchyMap(employee: List<Employee>): Map<String, Any> {
        return employee.associateBy({ it.name }) { employeesToHierarchyMap(it.subordinates) }
    }
}
