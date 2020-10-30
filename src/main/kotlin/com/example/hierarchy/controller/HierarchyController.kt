package com.example.hierarchy.controller

import com.example.hierarchy.dto.Hierarchy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HierarchyController {
    @GetMapping("/employee-hierarchy")
    fun employeeHierarchy() = Hierarchy("Hello world")
}
