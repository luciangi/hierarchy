package com.example.hierarchy.repository

import com.example.hierarchy.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
internal class UserRepositoryTestIT {
    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * given a valid saved user
     * when calling findByUsername
     * then it returns the saved user
     */
    @Test
    fun findByUsername() {
        // Given
        val savedUser = User("username", "password")
        userRepository.save(savedUser)

        // When
        val resultUser = userRepository.findByUsername(savedUser.username)

        // Then
        assertThat(savedUser).isNotNull
        Assertions.assertEquals(savedUser, resultUser)
        Assertions.assertEquals(savedUser.username, resultUser!!.username)
        Assertions.assertEquals(savedUser.password, resultUser.password)
    }
}
