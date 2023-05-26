package ru.tastypizza.apigateway.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class CustomerConfig {
    @Bean
    @LoadBalanced
    fun restTemplate(): RestTemplate{
        return RestTemplate()
    }

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("profile-service") {
                it.path("/auth/**", "/profile", "/public_key")
                    .uri("lb://profile")
            }
            .route("menu-service") {
                it.path("/menu/**", "/orders/**", "/restaurant/**")
                    .uri("lb://menu-orders")
            }
            .build()
    }
}