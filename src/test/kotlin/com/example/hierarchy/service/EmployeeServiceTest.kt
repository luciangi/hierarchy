package com.example.hierarchy.service

import com.example.hierarchy.exception.InvalidHierarchyException
import com.example.hierarchy.model.Employee
import com.example.hierarchy.repository.EmployeeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class EmployeeServiceTest {
    @Mock
    private lateinit var employeeRepository: EmployeeRepository

    @InjectMocks
    private lateinit var employeeService: EmployeeService

    /**
     * given an invalid employee hierarchy with multiple roots
     * when calling buildEmployeeHierarchy
     * then the method should throw an InvalidHierarchyException
     */
    @Test
    fun buildEmployeeHierarchyShouldThrowExceptionForMultipleRootsError() {
        // Given
        val employeeNameToSupervisorName = mapOf(
                "Nick" to "Barbara",
                "Sophie" to "Jonas"
        )

        // When
        val exception: Exception = Assertions.assertThrows(InvalidHierarchyException::class.java) {
            employeeService.buildEmployeeHierarchy(employeeNameToSupervisorName)
        }

        // Then
        Assertions.assertNotNull(exception.message)
        employeeNameToSupervisorName.values.forEach {
            Assertions.assertTrue(exception.message!!.contains(it))
        }
    }

    /**
     * given an invalid employee hierarchy with no roots
     * when calling buildEmployeeHierarchy
     * then the method should throw an InvalidHierarchyException
     */
    @Test
    fun buildEmployeeHierarchyShouldThrowExceptionForNoRootsError() {
        // Given
        val employeeNameToSupervisorName = mapOf(
                "Jonas" to "Barbara",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
        )

        // When
        val exception: Exception = Assertions.assertThrows(InvalidHierarchyException::class.java) {
            employeeService.buildEmployeeHierarchy(employeeNameToSupervisorName)
        }

        // Then
        Assertions.assertNotNull(exception.message)
    }

    /**
     * given a valid employee hierarchy
     * when calling buildEmployeeHierarchy
     * then the a valid root Employee object is returned
     */
    @Test
    fun buildEmployeeHierarchyShouldReturnCorrectHierarchy() {
        // Given
        val employeeNameToSupervisorName = mapOf(
                "Pete" to "Nick",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
        )

        // When
        val rootEmployee = employeeService.buildEmployeeHierarchy(employeeNameToSupervisorName)

        // Then
        assertEquals(rootEmployee.name, "Jonas")
        assertEquals(rootEmployee.supervisor, null)
        assertEquals(rootEmployee.subordinates.size, 1)

        val sophie = rootEmployee.subordinates.find { it.name == "Sophie" }
        assertThat(sophie).isNotNull
        assertEquals(sophie!!.supervisor!!.name, "Jonas")
        assertEquals(sophie.subordinates.size, 1)

        val nick = sophie.subordinates.find { it.name == "Nick" }
        assertThat(nick).isNotNull
        assertEquals(nick!!.supervisor!!.name, "Sophie")
        assertEquals(nick.subordinates.size, 2)

        val barbara = nick.subordinates.find { it.name == "Barbara" }
        assertThat(barbara).isNotNull
        assertEquals(barbara!!.supervisor!!.name, "Nick")
        assertEquals(barbara.subordinates.size, 0)

        val pete = nick.subordinates.find { it.name == "Pete" }
        assertThat(pete).isNotNull
        assertEquals(pete!!.supervisor!!.name, "Nick")
        assertEquals(pete.subordinates.size, 0)
    }

    /**
     * given a valid employee
     * when calling saveEmployeeHierarchy
     * then the employee is saved and the function returns the save entity
     */
    @Test
    fun saveEmployeeHierarchyShouldReturnSavedEntity() {
        // Given
        val employee = Employee("employeeName", null)
        given(employeeRepository.save(employee)).willReturn(employee)

        // When
        val savedEmployee = employeeService.saveEmployeeHierarchy(employee)

        // Then
        verify(employeeRepository, times(1)).deleteAll()
        verify(employeeRepository, times(1)).save(employee)
        verify(employeeRepository, times(1)).flush()
        verifyNoMoreInteractions(employeeRepository)

        assertEquals(savedEmployee.name, employee.name)
    }

    /**
     * given a valid employee hierarchy
     * when calling employeesToHierarchyMap
     * then the a valid hierarchy map is returned
     */
    @Test
    fun employeesToMapShouldReturnCorrectResult() {
        // Given
        val root = Employee("root", null)
        val e21 = Employee("e21", root)
        val e22 = Employee("e22", root)
        root.subordinates.add(e21)
        root.subordinates.add(e22)
        val e31 = Employee("e31", e21)
        e21.subordinates.add(e31)

        // When
        val employeeMap = employeeService.employeesToHierarchyMap(arrayListOf(root))

        // Then
        assertEquals(employeeMap, mapOf(
                root.name to mapOf(
                        e21.name to mapOf(
                                e31.name to emptyMap<String, String>()
                        ),
                        e22.name to emptyMap()
                )
        ))
    }
}
