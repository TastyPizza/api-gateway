package ru.tastypizza.apigateway.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter (
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter () {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt: String = getJwtFromRequest(request)
        
        if (StringUtils.hasText(jwt) &&
            jwtTokenProvider.validateToken(jwt) &&
            jwtTokenProvider.getTokenType(jwt) != "refresh"
            ) {
            val username = jwtTokenProvider.getUserLoginFromToken(jwt)
            //val tokenType = jwtTokenProvider.getTokenType(jwt)
            val role = jwtTokenProvider.getRole(jwt) 
            
            val authentication = UsernamePasswordAuthenticationToken(
                username,
                "",
                setOf(SimpleGrantedAuthority(role))
            )
            
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        
        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String {
        val bearerToken = request.getHeader("Authorization")
        
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else ""
    }
}