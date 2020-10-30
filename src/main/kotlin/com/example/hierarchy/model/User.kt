package com.example.hierarchy.model

import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Transient
import javax.persistence.Column
import javax.persistence.Entity
import javax.validation.constraints.NotBlank

@Entity
data class User(
        @Column(unique = true, nullable = false) @NotBlank val username: String,
        @Column(nullable = false) @NotBlank @Length(min = 5) @Transient val password: String
) : CoreEntity()
