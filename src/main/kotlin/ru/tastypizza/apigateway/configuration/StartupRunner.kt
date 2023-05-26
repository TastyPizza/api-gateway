package ru.tastypizza.apigateway.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class StartupRunner : ApplicationRunner {
    private val log: Logger = LoggerFactory.getLogger(StartupRunner::class.java)
    @Autowired
    lateinit var restTemplate: RestTemplate

    //		Will try to get public key from profile service on application startup
    override fun run(args: ApplicationArguments?) {
        try {
            val response = restTemplate.getForObject("http://profile/public_key", String::class.java)
            log.info("Successfully got a public key from PROFILE service: {}", response)
        } catch (ex : Exception){
            log.error(ex.message)
        }
    }
}
