package com.example.hierarchy.service

import com.example.hierarchy.model.User
import com.example.hierarchy.repository.UserRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException

internal class UserDetailsServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val userDetailsService = UserDetailsService(userRepository)

    /**
     * given a username that is not present in the userRepository
     * when calling loadUserByUsername
     * then a UsernameNotFoundException is thrown
     */
    @Test
    fun loadUserByUsernameShouldThrowUsernameNotFoundException() {
        // Given
        val username = "invalidUsername"
        every { userRepository.findByUsername(username) } returns null

        // When
        val exception: Exception = assertThrows(UsernameNotFoundException::class.java) {
            userDetailsService.loadUserByUsername(username)
        }

        // Then
        verify(exactly = 1) { userRepository.findByUsername(username) }
        confirmVerified(userRepository)

        assertTrue(exception.message!!.contains(username))
    }

    /**
     * given a valid username that can be found in the userRepository
     * when calling loadUserByUsername
     * then the correct userDetails are returned
     */
    @Test
    fun loadUserByUsernameShouldReturnCorrectUserDetails() {
        // Given
        val user = User("username", "password")
        every { userRepository.findByUsername(user.username) } returns user

        // When
        val userPrincipal = userDetailsService.loadUserByUsername(user.username)

        // Then
        verify(exactly = 1) { userRepository.findByUsername(user.username) }
        confirmVerified(userRepository)

        assertEquals(userPrincipal.username, user.username)
        assertEquals(userPrincipal.password, user.password)
        assertTrue(userPrincipal.authorities.isEmpty())
        assertTrue(userPrincipal.isEnabled)
        assertTrue(userPrincipal.isCredentialsNonExpired)
        assertTrue(userPrincipal.isAccountNonExpired)
        assertTrue(userPrincipal.isAccountNonLocked)
    }
}
