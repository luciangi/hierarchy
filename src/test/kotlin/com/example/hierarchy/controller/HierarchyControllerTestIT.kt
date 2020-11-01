package com.example.hierarchy.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(HierarchyController::class)
internal class HierarchyControllerTestIT(@Autowired private val mockMvc: MockMvc) {
    /**
     * when executing a GET request on the "/employee-hierarchy" endpoint
     * then the appropriate result is returned
     */
    @Test
    @WithMockUser(username = "mock", password = "mock")
    fun employeeHierarchyEndpointShouldReturnResult() {
        // When
        val result = mockMvc.perform(get("/employee-hierarchy").accept(APPLICATION_JSON))
                .andExpect(status().isOk)

        // Then
        result.andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value("Hello world"))
    }
}
