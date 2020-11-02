package com.example.hierarchy.repository

import com.example.hierarchy.model.Employee
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.stream.Stream

@DataJpaTest
internal class EmployeeRepositoryTestIT(@Autowired private val employeeRepository: EmployeeRepository) {
    companion object {
        @JvmStatic
        fun provideEmployeeNamesForGetSupervisorsShouldReturnAppropriateResult(): Stream<Arguments?>? {
            return Stream.of(
                    Arguments.of("employeeName", "supervisorName", "supervisorsSupervisorName"),
                    Arguments.of("employeeName", "supervisorName", null),
                    Arguments.of("employeeName", null, null)
            )
        }
    }

    /**
     * given a non existent employee name
     * when calling getSupervisors
     * then it returns null
     */
    @Test
    fun findByUsernameShouldReturnNull() {
        // Given
        val employeeName = "invalidUsername"

        // When
        val employeeProjection = employeeRepository.getEmployeeSuperiors(employeeName)

        // Then
        assertThat(employeeProjection).isNull()
    }

    /**
     * given a valid employee hierarchy
     * when calling getSupervisors
     * then it returns the employee projection
     */
    @ParameterizedTest
    @MethodSource("provideEmployeeNamesForGetSupervisorsShouldReturnAppropriateResult")
    fun getSupervisorsShouldReturnAppropriateResult(employeeName: String, supervisorName: String?, supervisorsSupervisorName: String?) {
        // Given
        val savedSupervisorsSupervisor = if (supervisorsSupervisorName == null) null else Employee(supervisorsSupervisorName, null)
        val savedSupervisor = if (supervisorName == null) null else Employee(supervisorName, savedSupervisorsSupervisor)
        savedSupervisorsSupervisor?.subordinates?.add(savedSupervisor!!)
        val savedEmployee = Employee(employeeName, savedSupervisor)
        savedSupervisor?.subordinates?.add(savedEmployee)

        val rootEmployee: Employee = savedSupervisorsSupervisor ?: (savedSupervisor ?: savedEmployee)

        employeeRepository.save(rootEmployee)

        // When
        val employeeProjection = employeeRepository.getEmployeeSuperiors(employeeName)

        // Then
        assertThat(employeeProjection).isNotNull
        assertEquals(employeeProjection!!.getName(), savedEmployee.name)
        assertEquals(employeeProjection.getSupervisor(), supervisorName)
        assertEquals(employeeProjection.getSupervisorsSupervisor(), supervisorsSupervisorName)
    }
}
