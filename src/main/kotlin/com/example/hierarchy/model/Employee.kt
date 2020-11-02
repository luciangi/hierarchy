package com.example.hierarchy.model

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Employee(
        @Column(unique = true, nullable = false)
        @NotBlank val name: String,
        @JoinColumn(name = "supervisor_id")
        @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var supervisor: Employee?,
        @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        val subordinates: MutableList<Employee> = mutableListOf()
) : CoreEntity()
