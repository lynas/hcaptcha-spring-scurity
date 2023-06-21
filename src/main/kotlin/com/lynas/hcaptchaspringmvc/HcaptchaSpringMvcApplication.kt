package com.lynas.hcaptchaspringmvc

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID
import javax.persistence.*


@SpringBootApplication
class HcaptchaSpringMvcApplication {
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
class HomeController {

	@RequestMapping("/")
	fun home(): String {
		return "home"
	}
	@RequestMapping("/login")
	fun login(): String {
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