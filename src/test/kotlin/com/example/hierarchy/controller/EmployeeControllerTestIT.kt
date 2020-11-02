package com.example.hierarchy.controller

import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(EmployeeController::class)
//TODO: mock data
internal class EmployeeControllerTestIT(@Autowired private val mockMvc: MockMvc) {
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
    fun employeeEndpointShouldReturnNotFoundError() {
        // Given
        val employeeName = "invalidName"

        // When
        val response = mockMvc.perform(get("/employee")
                .param("name", employeeName)
                .accept(APPLICATION_JSON))

        // Then
        response.andExpect(status().isNotFound)
                .andExpect(content().contentType(APPLICATION_JSON))
                //    TODO: add proper message
                .andExpect(jsonPath("$.message").value("Employee was not found"))
    }

    /**
     * given a valid employee name
     * when executing a GET request on the "/employee" endpoint using the given name
     * then a response containing the supervisor name and the supervisor's supervisor name
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeEndpointShouldReturnResponse() {
        // Given
        val employeeName = "Nick"

        // When
        val response = mockMvc.perform(get("/employee")
                .param("name", employeeName)
                .accept(APPLICATION_JSON))

        // Then
        response.andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content()
                        .json(JSONObject("""
                            {
                                "name": "Nick",
                                "supervisor": "Sophie",
                                "supervisorsSupervisor": "Jonas"
                            }
                            """.trimIndent()).toString()))
    }
}
