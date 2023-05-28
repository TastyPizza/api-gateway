package ru.tastypizza.apigateway.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*


@Component
class PublicKeyService(
    @Qualifier("redisTemplate") private val redisTemplate: RedisTemplate<Any, Any>
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["public-key"])
    fun receiveMessage(publicKeyString: String) {
        log.info("Received public key: $publicKeyString")
        savePublicKey("public_key", getPublicKeyFromString(publicKeyString.substring(1, publicKeyString.length - 1)))
    }

    fun savePublicKey(publicKeyIdentifier: String, publicKey: PublicKey): PublicKey {
        // Save the public key to the Redis cache
        log.info("Trying to save public key")
        redisTemplate.opsForValue().set(publicKeyIdentifier, publicKey)
        return publicKey
    }

    fun getPublicKey(publicKeyIdentifier: String): PublicKey? {
        // Retrieve the public key from the Redis cache
        val publicKey = redisTemplate.opsForValue().get(publicKeyIdentifier)
        log.info("Retrieved public key from cache")
        return publicKey as PublicKey
    }

    fun getPublicKeyFromString(publicKeyString: String): PublicKey {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        return keyFactory.generatePublic(publicKeySpec)
    }
}

