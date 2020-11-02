package com.example.hierarchy.projection

interface EmployeeProjection {
    fun getName(): String
    fun setName(name: String): String

    fun getSupervisor(): String
    fun setSupervisor(supervisor: String): String

    fun getSupervisorsSupervisor(): String
    fun setSupervisorsSupervisor(supervisorsSupervisor: String)
}
