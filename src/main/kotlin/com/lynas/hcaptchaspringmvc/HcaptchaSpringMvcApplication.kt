package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.annotation.JsonProperty
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.persistence.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession


@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class HcaptchaSpringMvcApplication {
	@Bean
	fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

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

@Controller
class HomeController(
	private val restTemplate: RestTemplate,
	private val userRepository: UserRepository
) {

	private val logger = KotlinLogging.logger {}

	@Value("\${hCaptcha.secret.key}")
	private lateinit var hCaptchaSecretKey: String

	@RequestMapping("/")
	fun home(request: HttpServletRequest): String {
		return "home"
	}
	@RequestMapping("/login")
	fun login(): String {
		return "login"
	}


	@PostMapping("/loginUser")
	fun loginPost(
		@RequestParam("h-captcha-response") captchaResponse: String,
		@ModelAttribute appUserDto: AppUserDto,
		request: HttpServletRequest
	): String {
		logger.info { captchaResponse }
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
		val map = LinkedMultiValueMap<String, String>().apply {
			add("response", captchaResponse)
			add("secret", hCaptchaSecretKey)
		}

		val apiRequest = HttpEntity<MultiValueMap<String, String>>(map, headers)
		val url = "https://hcaptcha.com/siteverify"
		val isCaptchaSuccess = try{
			restTemplate.postForEntity(url, apiRequest, HCaptchaResponse::class.java).body?.success ?: false
		} catch (ex: HttpClientErrorException){
			logger.error("hcaptcha error", ex)
			true
		}
		if (!isCaptchaSuccess) {
			throw RuntimeException("invalid captcha")
		}

		val appUser = userRepository.findByUsername(appUserDto.username)
		val isValidUser = checkPassword(appUserDto.password, appUser?.password ?: "")
		if (!isValidUser) {
			throw RuntimeException("invalid user or password")
		}

		val sc = SecurityContextHolder.getContext()
		sc.authentication = UsernamePasswordAuthenticationToken("username", "password",
			AuthorityUtils.createAuthorityList("ROLE_USER"))
		val session: HttpSession = request.getSession(true)
		session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc)
		return "redirect:/"
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

data class AppUserDto(
	val username: String,
	val password: String
)


interface UserRepository : JpaRepository<AppUser, String> {
	fun findByUsername(username: String): AppUser?
}

data class HCaptchaResponse(
	val success: Boolean,
	@JsonProperty(value = "error-codes")
	val errorCodes: List<String> = listOf()
)

fun checkPassword(passwordClean: String, passwordHash: String): Boolean = BCrypt.checkpw(passwordClean, passwordHash)
