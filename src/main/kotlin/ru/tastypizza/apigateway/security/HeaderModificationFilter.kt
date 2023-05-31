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
//             Access the authentication and modify the headers
            val modifiedRequest = exchange.request.mutate()
                .headers { headers ->
                    headers.set("User-Id", authentication.principal.toString())
                    headers.set("Role", authentication.authorities.joinToString(","))
                }
                .build()
            chain.filter(exchange.mutate().request(modifiedRequest).build())
        }.switchIfEmpty(chain.filter(exchange))
    }
}