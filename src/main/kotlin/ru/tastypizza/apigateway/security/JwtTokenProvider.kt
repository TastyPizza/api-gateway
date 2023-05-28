package ru.tastypizza.apigateway.security

import io.jsonwebtoken.*
import org.bouncycastle.util.Objects
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import ru.tastypizza.apigateway.service.PublicKeyService
import java.security.PublicKey


class JwtTokenProvider {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var publicKeyService: PublicKeyService

    fun validateToken(token: String): Boolean {
        try {
            val publicKey: PublicKey? = publicKeyService.getPublicKey("public_key")
            if (Objects.areEqual(publicKey, null))
                log.error("Cannot validate token, because not received public key!")
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
        val publicKey: PublicKey? = publicKeyService.getPublicKey("public_key")
        if (Objects.areEqual(publicKey, null))
            log.error("Cannot validate token, because not received public key!")

        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body.subject
    }

    fun getTokenType(token: String): String {
        val publicKey: PublicKey? = publicKeyService.getPublicKey("public_key")
        if (Objects.areEqual(publicKey, null))
            log.error("Cannot validate token, because not received public key!")
        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body["tokenType"] as String
    }

    fun getRole(token: String): String {
        val publicKey: PublicKey? = publicKeyService.getPublicKey("public_key")
        if (Objects.areEqual(publicKey, null))
            log.error("Cannot validate token, because not received public key!")
        return Jwts
            .parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(token)
            .body["role"] as String
    }
}