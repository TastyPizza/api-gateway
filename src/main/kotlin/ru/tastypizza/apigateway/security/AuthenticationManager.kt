package ru.tastypizza.apigateway.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono

class AuthenticationManager(
    private var jwtTokenProvider: JwtTokenProvider
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val jwt: String = authentication.credentials.toString()
        println(jwt)
        if (StringUtils.hasText(jwt) &&
            jwtTokenProvider.validateToken(jwt) &&
            jwtTokenProvider.getTokenType(jwt) != "refresh"
        ) {
            val username = jwtTokenProvider.getUserLoginFromToken(jwt)
            val role = jwtTokenProvider.getRole(jwt)

            val authenticationToken = UsernamePasswordAuthenticationToken(
                username,
                "",
                setOf(SimpleGrantedAuthority(role))
            )
            println(authenticationToken)
            return Mono.just(authenticationToken)
        } else {
            return Mono.empty()
        }
    }
}
