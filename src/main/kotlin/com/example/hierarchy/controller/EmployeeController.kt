package com.example.hierarchy.controller

import com.example.hierarchy.exception.EmployeeNotFoundException
import com.example.hierarchy.projection.EmployeeProjection
import com.example.hierarchy.repository.EmployeeRepository
import com.example.hierarchy.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/employee")
class EmployeeController(
        @Autowired
        private val employeeService: EmployeeService,
        @Autowired
        private val employeeRepository: EmployeeRepository
) {
    @PostMapping(
            path = ["/hierarchy"],
            consumes = [APPLICATION_JSON_VALUE],
            produces = [APPLICATION_JSON_VALUE]
    )
    fun postEmployeeHierarchy(@RequestBody requestData: Map<String, String>): Map<String, Any> {
        val rootEmployee = employeeService.buildEmployeeHierarchy(requestData)
        val savedEmployee = employeeService.saveEmployeeHierarchy(rootEmployee)

        return employeeService.employeesToMap(listOf(savedEmployee))
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getEmployee(@RequestParam("name") name: String): EmployeeProjection? {
        return employeeRepository.getEmployeeSuperiors(name)
                ?: throw EmployeeNotFoundException("Cannot find employee with name: $name")
    }
}
