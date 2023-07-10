package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.annotation.JsonProperty
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
class HomeController(
    private val userRepository: UserRepository,
    private val apiCallService: ApiCallService
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
        val isCaptchaSuccess = apiCallService.makeApiCall(captchaResponse)
        if (!isCaptchaSuccess) {
            throw RuntimeException("invalid captcha")
        }
        logger.info { isCaptchaSuccess }
        val appUser = userRepository.findByUsername(appUserDto.username)
        val isValidUser = checkPassword(appUserDto.password, appUser?.password ?: "")
        if (!isValidUser) {
            throw RuntimeException("invalid user or password")
        }

        val sc = SecurityContextHolder.getContext()
        sc.authentication = UsernamePasswordAuthenticationToken("username", "password",
            AuthorityUtils.createAuthorityList("ROLE_USER"))
        val session: HttpSession = request.getSession(true)
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc)
        return "redirect:/"
    }
}

data class HCaptchaResponse(
    val success: Boolean,
    @JsonProperty(value = "error-codes")
    val errorCodes: List<String> = listOf()
)

fun checkPassword(passwordClean: String, passwordHash: String): Boolean = BCrypt.checkpw(passwordClean, passwordHash)
