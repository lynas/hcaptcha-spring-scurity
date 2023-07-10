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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HcaptchaSpringMvcApplicationTests {

    @Autowired
    private lateinit var apiCallService: ApiCallService
    private lateinit var mockWebServer: MockWebServer

    @BeforeAll
    fun setup(){
        mockWebServer = MockWebServer()
        mockWebServer.start(55444)
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
}


