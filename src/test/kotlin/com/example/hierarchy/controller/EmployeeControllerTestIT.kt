package com.example.hierarchy.controller

import com.example.hierarchy.projection.EmployeeProjection
import com.example.hierarchy.repository.EmployeeRepository
import com.example.hierarchy.service.EmployeeService
import org.hamcrest.core.StringContains
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.projection.SpelAwareProxyProjectionFactory
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.util.LinkedMultiValueMap


@WebMvcTest(EmployeeController::class)
//TODO: mock data
internal class EmployeeControllerTestIT(@Autowired private val mockMvc: MockMvc) {
    @MockBean
    private lateinit var employeeService: EmployeeService

    @MockBean
    private lateinit var employeeRepository: EmployeeRepository

    /**
     * given an invalid circular employee hierarchy in a flat json
     * when executing a POST request on the "/employee/hierarchy" endpoint using the given payload
     * then the endpoint responds with an error message
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
//    TODO: check for multiple roots in the middle of the tree
    fun employeeHierarchyEndpointShouldReturnMultipleRootsError() {
        // Given
        val hierarchy = JSONObject(mapOf(
                "Pete" to "Nick",
                "Sophie" to "Jonas"
        ))

        // When
        val response = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(hierarchy.toString())
                .accept(APPLICATION_JSON))

        // Then
        response.andExpect(status().isBadRequest)
                .andExpect(content().contentType(APPLICATION_JSON))
                //    TODO: add proper message
                .andExpect(jsonPath("$.message").value("Multiple roots"))
    }

    /**
     * given an invalid multiple root employee hierarchy in a flat json
     * when executing a POST request on the "/employee/hierarchy" endpoint using the given payload
     * then the endpoint responds with an error message
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeHierarchyEndpointShouldReturnCircularError() {
        // Given
        val hierarchy = JSONObject(mapOf(
                "Pete" to "Nick",
                "Nick" to "Pete"
        ))

        // When
        val response = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(hierarchy.toString())
                .accept(APPLICATION_JSON))

        // Then
        response.andExpect(status().isBadRequest)
                .andExpect(content().contentType(APPLICATION_JSON))
                //    TODO: add proper message
                .andExpect(jsonPath("$.message").value("Circular payload"))
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
        val hierarchy = JSONObject(mapOf(
                "Pete" to "Nick",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
        ))

        // When
        val response = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(hierarchy.toString())
                .accept(APPLICATION_JSON))

        // Then
        val expectedResponseContent = JSONObject(mapOf(
                "Jonas" to mapOf(
                        "Sophie" to mapOf(
                                "Nick" to mapOf(
                                        "Pete" to emptyMap<String, String>(),
                                        "Barbara" to emptyMap()
                                )
                        )
                )
        ))

        response.andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent.toString()))
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

        given(employeeRepository.getEmployeeSuperiors(employeeName))
                .willReturn(employeeProjection)

        // When
        val response = mockMvc.get("/employee") {
            accept = APPLICATION_JSON
            params = LinkedMultiValueMap<String, String>(mapOf("name" to listOf(employeeName)))
        }

        // Then
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
