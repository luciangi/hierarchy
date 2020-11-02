package com.example.hierarchy.projection

interface EmployeeProjection {
    fun getName(): String
    fun getSupervisor(): String
    fun getSupervisorsSupervisor(): String
}
