package ru.tastypizza.apigateway.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class ModifyHeaderFilter () : OncePerRequestFilter () {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        response.addHeader("username", SecurityContextHolder.getContext().authentication.name)
        response.addHeader("role", SecurityContextHolder.getContext().authentication.authorities.toString())
        filterChain.doFilter(request, response)
    }
}