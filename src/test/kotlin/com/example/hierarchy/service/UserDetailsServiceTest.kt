package com.example.hierarchy.service

import com.example.hierarchy.model.User
import com.example.hierarchy.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockitoExtension::class)
internal class UserDetailsServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userDetailsService: UserDetailsService

    /**
     * given a username that is not present in the userRepository
     * when calling loadUserByUsername
     * then a UsernameNotFoundException is thrown
     */
    @Test
    fun loadUserByUsernameShouldThrowUsernameNotFoundException() {
        // Given
        val username = "invalidUsername"
        given(userRepository.findByUsername(username)).willReturn(null)

        // When
        val exception: Exception = assertThrows(UsernameNotFoundException::class.java) {
            userDetailsService.loadUserByUsername(username)
        }

        // Then
        verify(userRepository, times(1)).findByUsername(username)
        verifyNoMoreInteractions(userRepository)

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
        given(userRepository.findByUsername(user.username)).willReturn(user)

        // When
        val userPrincipal = userDetailsService.loadUserByUsername(user.username)

        // Then
        verify(userRepository, times(1)).findByUsername(user.username)
        verifyNoMoreInteractions(userRepository)

        assertEquals(userPrincipal.username, user.username)
        assertEquals(userPrincipal.password, user.password)
        assertTrue(userPrincipal.authorities.isEmpty())
        assertTrue(userPrincipal.isEnabled)
        assertTrue(userPrincipal.isCredentialsNonExpired)
        assertTrue(userPrincipal.isAccountNonExpired)
        assertTrue(userPrincipal.isAccountNonLocked)
    }
}
