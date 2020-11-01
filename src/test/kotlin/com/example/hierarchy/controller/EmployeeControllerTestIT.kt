package com.example.hierarchy.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
        val employeeHierarchy = """
                        {
                            "Pete": "Nick",
                            "Sophie": "Jonas"
                        }
                        """.trimIndent()
        // When
        val result = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(employeeHierarchy)
                .accept(APPLICATION_JSON))

        // Then
        result.andExpect(status().isBadRequest)
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
        val employeeHierarchy = """
                        {
                            "Pete": "Nick",
                            "Nick": "Pete"
                        }
                        """.trimIndent()

        // When
        val result = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(employeeHierarchy)
                .accept(APPLICATION_JSON))

        // Then
        result.andExpect(status().isBadRequest)
                .andExpect(content().contentType(APPLICATION_JSON))
                //    TODO: add proper message
                .andExpect(jsonPath("$.message").value("Circular payload"))
    }

    /**
     * given a valid employee hierarchy in a flat json
     * when executing a POST request on the "/employee/hierarchy" endpoint using the given payload
     * then the result is the employee hierarchy in a tree json
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeHierarchyEndpointShouldReturnResult() {
        // Given
        val employeeHierarchy = """
                        {
                            "Pete": "Nick",
                            "Barbara": "Nick",
                            "Nick": "Sophie",
                            "Sophie": "Jonas"
                        }
                        """.trimIndent()

        // When
        val result = mockMvc.perform(post("/employee/hierarchy")
                .contentType(APPLICATION_JSON)
                .content(employeeHierarchy)
                .accept(APPLICATION_JSON))

        // Then
        result.andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content()
                        .string("""
                                {
                                    "Jonas": {
                                        "Sophie": {
                                            "Nick": {
                                                "Pete": {},
                                                "Barbara": {}
                                            }
                                        }
                                    }
                                }
                                """))
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
        val result = mockMvc.perform(get("/employee")
                .param("name", employeeName)
                .accept(APPLICATION_JSON))

        // Then
        result.andExpect(status().isNotFound)
                .andExpect(content().contentType(APPLICATION_JSON))
                //    TODO: add proper message
                .andExpect(jsonPath("$.message").value("Employee was not found"))
    }

    /**
     * given a valid employee name
     * when executing a GET request on the "/employee" endpoint using the given name
     * then a result containing the supervisor name and the supervisor's supervisor name
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeEndpointShouldReturnResult() {
        // Given
        val employeeName = "Nick"

        // When
        val result = mockMvc.perform(get("/employee")
                .param("name", employeeName)
                .accept(APPLICATION_JSON))

        // Then
        result.andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content()
                        .string("""
                            {
                                "name": "Nick"
                                "supervisor": "Sophie",
                                "supervisorsSupervisor": "Jonas"
                            }
                            """))
    }
}
