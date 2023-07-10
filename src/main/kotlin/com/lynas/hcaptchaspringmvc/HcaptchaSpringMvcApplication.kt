package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.handler.logging.LogLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.util.UUID


@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class HcaptchaSpringMvcApplication {

	@Value("\${hCaptcha.site}")
	private lateinit var baseUrl: String

	@Bean
	fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

	fun buildLoggingWebClient(objectMapper: ObjectMapper): WebClient {
		val httpClient = httpClient()

		return WebClient
			.builder()
			.baseUrl(baseUrl)
			.codecs { codec ->
				codec.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
				codec.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
			}
			.clientConnector(ReactorClientHttpConnector(httpClient))
			.build()
	}

	@Bean
	fun webClient(objectMapper: ObjectMapper): WebClient = buildLoggingWebClient(objectMapper)

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
	runApplication<HcaptchaSpringMvcApplication>(*args)
}







