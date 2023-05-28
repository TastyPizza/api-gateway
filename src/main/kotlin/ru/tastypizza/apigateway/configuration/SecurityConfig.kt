package ru.tastypizza.apigateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ru.tastypizza.apigateway.security.AuthenticationManager
import ru.tastypizza.apigateway.security.HeaderModificationFilter
import ru.tastypizza.apigateway.security.JwtTokenProvider
import ru.tastypizza.apigateway.security.SecurityContextRepository
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*


@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class SecurityConfig {


    @Bean
    fun jwtTokenProvider(): JwtTokenProvider {
        return JwtTokenProvider()
    }

    @Bean
    fun authManager(jwtTokenProvider: JwtTokenProvider): ReactiveAuthenticationManager {
        return AuthenticationManager(jwtTokenProvider)
    }

    @Bean
    fun securityContextRepository(authManager: ReactiveAuthenticationManager): SecurityContextRepository {
        return SecurityContextRepository(authManager)
    }

    @Bean
    fun securityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain? {
        return httpSecurity
            .exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, _: AuthenticationException? ->
                Mono.fromRunnable {
                    swe.response.statusCode = HttpStatus.UNAUTHORIZED
                }
            }
            .accessDeniedHandler { swe: ServerWebExchange, _: AccessDeniedException? ->
                Mono.fromRunnable {
                    swe.response.statusCode = HttpStatus.FORBIDDEN
                }
            }
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authManager(jwtTokenProvider()))
            .securityContextRepository(securityContextRepository(authManager(jwtTokenProvider())))
            .authorizeExchange()
            .pathMatchers("/auth/sign-in", "/auth/sign-up", "/public_key").permitAll()
            .pathMatchers("/auth/verification").hasAuthority("ROLE_UNVERIFIED_CLIENT")
            .pathMatchers("/profile/**", "/orders/**", "/menu/**", "/restaurant/**").hasAuthority("ROLE_VERIFIED_CLIENT")
            .anyExchange()
            .authenticated()
            .and()
            .addFilterAt(HeaderModificationFilter(), SecurityWebFiltersOrder.LAST)
            .build()
    }
}