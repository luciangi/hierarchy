package com.example.hierarchy.controller

import com.example.hierarchy.model.Employee
import com.example.hierarchy.repository.EmployeeRepository
import com.example.hierarchy.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee")
class EmployeeController(@Autowired private val employeeService: EmployeeService) {
    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @PostMapping(
            path = ["/hierarchy"],
            consumes = [APPLICATION_JSON_VALUE],
            produces = [APPLICATION_JSON_VALUE]
    )
    fun postEmployeeHierarchy(@RequestBody requestData: Map<String, String>): Map<String, Any> {
        val employees = requestData.entries.fold(mutableMapOf<String, Employee>()) { acc, entry ->
            acc[entry.key] = Employee(entry.key, null)

            if (!acc.containsKey(entry.value)) {
                acc[entry.value] = Employee(entry.value, null)
            }

            return@fold acc
        }

        employees.map { entry ->
            val supervisor = employees[requestData[entry.key]]
            if (supervisor != null) {
                entry.value.supervisor = supervisor
                supervisor.subordinates.add(entry.value)
            }
        }

        val rootEmployee = employees.values.find { it.supervisor == null }!!
        employeeRepository.deleteAll()
        employeeRepository.save(rootEmployee)

        return emptyMap()
    }
}
