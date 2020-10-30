package com.example.hierarchy.repository

import com.example.hierarchy.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String): User
}
