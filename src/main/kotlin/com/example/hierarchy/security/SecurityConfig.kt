package com.example.hierarchy.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
class SecurityConfig(@Autowired private val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http {
            httpBasic {}
            authorizeRequests {
                authorize("/**", authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            headers {
                frameOptions {
                    disable()
                }
            }
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder())
    }

    @Bean
    fun encoder(): PasswordEncoder? {
        return BCryptPasswordEncoder(11)
    }
}
