package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HcaptchaSpringMvcApplicationTests {

    @Autowired
    private lateinit var apiCallService: ApiCallService
    private lateinit var mockWebServer: MockWebServer

    @Value("\${hCaptcha.test.port}")
    private lateinit var port: String

    @BeforeAll
    fun setup(){
        mockWebServer = MockWebServer()
        mockWebServer.start(port.toInt())
    }

    @AfterAll
    fun tearDown(){
        mockWebServer.shutdown()
    }


    @Test
    fun shouldReturnTrueWhenSendingValidCaptcha() {
        val response: String = ObjectMapper().writeValueAsString(HCaptchaResponse(true))
        mockWebServer.enqueue(
            MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody(response))

        val res = apiCallService.makeApiCall("lll")
        assertEquals(true, res)

    }

    @Test
    fun shouldReturnFalseWhenSendingInValidCaptcha() {
        val response: String = ObjectMapper().writeValueAsString(HCaptchaResponse(false))
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(response))

        val res = apiCallService.makeApiCall("lll")
        assertEquals(false, res)

    }
}


