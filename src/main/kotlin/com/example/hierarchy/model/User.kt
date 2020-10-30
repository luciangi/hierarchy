package com.example.hierarchy.model

import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Transient
import javax.persistence.Column
import javax.persistence.Entity
import javax.validation.constraints.NotBlank

@Entity
class User(
        @Column(unique = true, nullable = false) @NotBlank var username: String,
        @Column(nullable = false) @NotBlank @Length(min = 5) @Transient var password: String
) : CoreEntity()
