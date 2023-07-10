package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.handler.logging.LogLevel
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.util.UUID


@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class AppRunner {

    @Bean
    fun webClient(objectMapper: ObjectMapper): WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient()))
        .build()

    private fun httpClient(): HttpClient {
        return HttpClient
            .create()
            .wiretap("reactor.netty.http.client.HttpClient", LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
    }

    @Bean
    fun runner(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        val user = AppUser().apply {
            id = UUID.randomUUID().toString()
            username = "admin"
            password = passwordEncoder.encode("admin")
        }
        userRepository.save(user)
    }

}

fun main(args: Array<String>) {
    runApplication<AppRunner>(*args)
}







