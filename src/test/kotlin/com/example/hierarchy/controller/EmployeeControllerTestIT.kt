package com.example.hierarchy.controller

import com.example.hierarchy.exception.InvalidHierarchyException
import com.example.hierarchy.model.Employee
import com.example.hierarchy.projection.EmployeeProjection
import com.example.hierarchy.repository.EmployeeRepository
import com.example.hierarchy.service.EmployeeService
import org.hamcrest.core.StringContains
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.util.LinkedMultiValueMap


@WebMvcTest(EmployeeController::class)
internal class EmployeeControllerTestIT(@Autowired private val mockMvc: MockMvc) {
    @MockBean
    private lateinit var employeeService: EmployeeService

    @MockBean
    private lateinit var employeeRepository: EmployeeRepository

    /**
     * given an invalid request payload
     * when executing a POST request on the "/employee/hierarchy" endpoint using the given payload and the employeeService
     * is throwing a InvalidHierarchyException
     * then the endpoint responds with an error message
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeHierarchyEndpointShouldReturnCircularError() {
        // Given
        val exceptionMessage = "exceptionMessage"
        val hierarchyMap = mapOf(
                "Sophie" to "Jonas",
                "Jonas" to "Jonas"
        )
        val hierarchy = JSONObject(hierarchyMap)

        given(employeeService.buildEmployeeHierarchy(hierarchyMap)).willThrow(InvalidHierarchyException(exceptionMessage))

        // When
        val response = mockMvc.post("/employee/hierarchy") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = hierarchy.toString()
        }

        // Then
        response.andExpect {
            status { isBadRequest }
            jsonPath("$.message").value(exceptionMessage)
        }
    }

    /**
     * given a valid employee hierarchy in a flat json
     * when executing a POST request on the "/employee/hierarchy" endpoint using the given payload
     * then the response is the employee hierarchy in a tree json
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeHierarchyEndpointShouldReturnResponse() {
        // Given
        val hierarchyMap = mapOf("Sophie" to "Jonas")
        val hierarchy = JSONObject(hierarchyMap)
        val responseMap = mapOf(
                "Jonas" to mapOf(
                        "Sophie" to emptyMap<String, String>()
                )
        )
        val expectedResponseContent = JSONObject(responseMap)
        val rootEmployee = Employee("Jonas", null)
        val savedEmployee = Employee("Jonas", null)

        given(employeeService.buildEmployeeHierarchy(hierarchyMap)).willReturn(rootEmployee)
        given(employeeService.saveEmployeeHierarchy(rootEmployee)).willReturn(savedEmployee)
        given(employeeService.employeesToHierarchyMap(listOf(savedEmployee))).willReturn(responseMap)

        // When
        val response = mockMvc.post("/employee/hierarchy") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = hierarchy.toString()
        }

        // Then
        verify(employeeService, times(1)).buildEmployeeHierarchy(hierarchyMap)
        verify(employeeService, times(1)).saveEmployeeHierarchy(rootEmployee)
        verify(employeeService, times(1)).employeesToHierarchyMap(listOf(savedEmployee))
        verifyNoMoreInteractions(employeeRepository)

        response.andExpect {
            status { isOk }
            content { contentType(APPLICATION_JSON) }
            content { string(expectedResponseContent.toString()) }
        }
    }

    /**
     * given a invalid employee name
     * when executing a GET request on the "/employee" endpoint using the given name
     * then the endpoints returns a not found error
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun getEmployeeEndpointShouldReturnNotFoundError() {
        // Given
        val employeeName = "invalidName"

        // When
        val response = mockMvc.get("/employee") {
            accept = APPLICATION_JSON
            params = LinkedMultiValueMap<String, String>(mapOf("name" to listOf(employeeName)))
        }

        // Then
        response.andExpect {
            status { isNotFound }
            jsonPath("$.message").value(StringContains(employeeName))
        }
    }

    /**
     * given a valid employee name
     * when executing a GET request on the "/employee" endpoint using the given name
     * then a response containing the supervisor name and the supervisor's supervisor name
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun getEmployeeEndpointShouldReturnResponse() {
        // Given
        val employeeName = "Nick"
        val supervisorName = "Sophie"
        val supervisorsSupervisorName = "Jonas"
        val employeeProjection: EmployeeProjection = SpelAwareProxyProjectionFactory()
                .createProjection(EmployeeProjection::class.java)
        employeeProjection.setName(employeeName)
        employeeProjection.setSupervisor(supervisorName)
        employeeProjection.setSupervisorsSupervisor(supervisorsSupervisorName)

        given(employeeRepository.getEmployeeSuperiors(employeeName)).willReturn(employeeProjection)

        // When
        val response = mockMvc.get("/employee") {
            accept = APPLICATION_JSON
            params = LinkedMultiValueMap<String, String>(mapOf("name" to listOf(employeeName)))
        }

        // Then
        verify(employeeRepository, times(1)).getEmployeeSuperiors(employeeName)
        verifyNoMoreInteractions(employeeRepository)

        response.andExpect {
            status { isOk }
            content { contentType(APPLICATION_JSON) }
            content {
                json(JSONObject("""
                            {
                                "name": "$employeeName",
                                "supervisor": "$supervisorName",
                                "supervisorsSupervisor": "$supervisorsSupervisorName"
                            }
                            """.trimIndent()).toString())
            }
        }
    }
}
