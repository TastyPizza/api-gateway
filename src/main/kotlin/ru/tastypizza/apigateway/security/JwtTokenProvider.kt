package ru.tastypizza.apigateway.security

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.security.PublicKey


class JwtTokenProvider (
    val publicKey: PublicKey
) {

    private val log = LoggerFactory.getLogger(this.javaClass)


    fun validateToken(token: String): Boolean {
        try {
            println(publicKey)
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            log.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            log.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            log.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            log.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            log.error("JWT claims string is empty.")
        }
        return false
    }
    
    fun getUserLoginFromToken(token: String): String {
        
        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body.subject
    }

    fun getTokenType(token: String): String {
        
        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body["tokenType"] as String
    }

    fun getRole(token: String): String {

        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body["role"] as String
    }
}