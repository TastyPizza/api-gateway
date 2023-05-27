package ru.tastypizza.apigateway.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class HeaderModificationFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authenticationMono = exchange.getPrincipal<Authentication>()

        return authenticationMono.flatMap { authentication ->
            // Access the authentication and modify the headers
            val modifiedHeaders = HttpHeaders()
            modifiedHeaders.addAll(exchange.request.headers)
            modifiedHeaders.add("User-Id", authentication.principal.toString())
            modifiedHeaders.add("Role", authentication.authorities.joinToString(","))

            val modifiedRequest = exchange.request.mutate()
                .headers { headers ->
                    headers.addAll(modifiedHeaders)
                }
                .build()

            chain.filter(exchange.mutate().request(modifiedRequest).build())
        }.switchIfEmpty(chain.filter(exchange))
    }
}