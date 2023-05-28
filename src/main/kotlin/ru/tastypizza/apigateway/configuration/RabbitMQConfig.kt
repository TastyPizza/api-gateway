package ru.tastypizza.apigateway.config

import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig(
    @Value("\${app.queue}")
    private val QUEUE: String,

    @Value("\${app.exchange}")
    private val EXCHANGE: String,

    @Value("\${app.routing-key}")
    private val ROUTING_KEY: String
) {
    @Bean
    fun queue(): Queue {
        return Queue(QUEUE)
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(EXCHANGE)
    }

    @Bean
    fun binding(queue: Queue?, exchange: TopicExchange?): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY)
    }
}