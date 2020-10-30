package com.example.hierarchy.service

import com.example.hierarchy.repository.UserRepository
import com.example.hierarchy.security.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService(@Autowired private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return UserPrincipal(userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username))
    }
}
