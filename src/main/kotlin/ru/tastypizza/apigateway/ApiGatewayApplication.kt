package ru.tastypizza.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
//	(exclude = [ReactiveSecurityAutoConfiguration::class, ReactiveUserDetailsServiceAutoConfiguration::class])
@EnableDiscoveryClient

class ApiGatewayApplication
	fun main(args: Array<String>) {
		runApplication<ApiGatewayApplication>(*args)
	}