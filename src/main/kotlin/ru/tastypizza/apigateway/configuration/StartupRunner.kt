package ru.tastypizza.apigateway.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.tastypizza.apigateway.service.PublicKeyService

@Component
class StartupRunner : ApplicationRunner {
    private val log: Logger = LoggerFactory.getLogger(StartupRunner::class.java)
    @Autowired
    lateinit var restTemplate: RestTemplate
    @Autowired
    lateinit var publicKeyService: PublicKeyService
    @Autowired
    lateinit var objectMapper: ObjectMapper

    //		Will try to get public key from profile service on application startup
    override fun run(args: ApplicationArguments?) {
        try {
            val response = restTemplate.getForObject("http://profile/public_key", String::class.java)
            val jsonObject = objectMapper.readValue(response, Map::class.java)
            val publicKey = jsonObject["encoded"] as String
            publicKeyService.savePublicKey("public_key", publicKeyService.getPublicKeyFromString(publicKey))

            log.info("Successfully got a public key from PROFILE service: {}", response)
        } catch (ex : Exception){
            log.error(ex.message)
        }
    }
}
