package com.lynas.hcaptchaspringmvc

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
//import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate
import java.util.UUID
import javax.persistence.*


@SpringBootApplication
class HcaptchaSpringMvcApplication {
	@Bean
	fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

//	@Bean
//	fun runner(
//		userRepository: UserRepository,
//		passwordEncoder: PasswordEncoder
//	) = CommandLineRunner {
//		val user = AppUser().apply {
//			id = UUID.randomUUID().toString()
//			username = "admin"
//			password = passwordEncoder.encode("admin")
//		}
//		userRepository.save(user)
//	}
}

fun main(args: Array<String>) {
	runApplication<HcaptchaSpringMvcApplication>(*args)
}

@Controller
class HomeController(
	private val restTemplate: RestTemplate
) {

	@Value("\${hCaptcha.secret.key}")
	private lateinit var hCaptchaSecretKey: String

	@RequestMapping("/")
	fun home(): String {
		return "home"
	}
	@RequestMapping("/login")
	fun login(): String {
		return "login"
	}


	@PostMapping("/login")
	fun loginPost(@RequestParam("h-captcha-response") captchaResponse: String): String {
		println(hCaptchaSecretKey)
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
		val map = LinkedMultiValueMap<String, String>()
		map.add("response", captchaResponse)
		map.add("secret", hCaptchaSecretKey)
		println(map)
		val request = HttpEntity<MultiValueMap<String, String>>(map, headers)
		val url = "https://hcaptcha.com/siteverify"
		val response = restTemplate.postForEntity(url, request, String::class.java)

		println("Post response")
		println(response)

		println("respones")
//		println(captchaResponse)
		return "login"
	}
}

@Entity
@Table(name = "app_user")
class AppUser {
	@Id
	lateinit var id: String
	@Column(nullable = false, unique = true)
	lateinit var username: String
	@Column(nullable = false)
	lateinit var password: String
}


interface UserRepository : JpaRepository<AppUser, String> {
	fun findByUsername(username: String): AppUser?
}