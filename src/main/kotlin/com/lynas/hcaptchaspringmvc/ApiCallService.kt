package com.lynas.hcaptchaspringmvc

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class ApiCallService(
    private val webClient: WebClient,
    @Value("\${hCaptcha.secret.key}")
    private var hCaptchaSecretKey: String,
    @Value("\${hCaptcha.site}")
    private var siteUrl: String
) {
    private val logger = KotlinLogging.logger {}


    fun makeApiCall(captchaResponse: String) : Boolean {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("response", captchaResponse)
            add("secret", hCaptchaSecretKey)
        }
        val rrr: Boolean =  webClient.post()
            .uri(siteUrl)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(HCaptchaResponse::class.java)
            .blockOptional()
            .map { it.success }
            .orElse(false)
        println("makeApiCall response")
        println(rrr)
        println("makeApiCall response")
        return rrr

    }

}