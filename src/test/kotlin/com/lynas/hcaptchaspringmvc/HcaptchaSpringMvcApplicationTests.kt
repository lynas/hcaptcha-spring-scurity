package com.lynas.hcaptchaspringmvc

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient


class HcaptchaSpringMvcApplicationTests {


	private lateinit var mockWebServer: MockWebServer
	private lateinit var webClient: WebClient
	private lateinit var apiCallService: ApiCallService

	@BeforeEach
	fun setUp() {
		mockWebServer = MockWebServer()
		mockWebServer.start()

		webClient = WebClient.builder()
			.baseUrl(mockWebServer.url("/").toString())
			.build()

		apiCallService = ApiCallService(webClient,RestTemplate(),"","")

	}

	@AfterEach
	fun tearDown() {
		mockWebServer.shutdown()
	}

	@Test
	fun shouldReturnTrueWhenSendingValidCaptcha() {


		val response: String = ObjectMapper().writeValueAsString(HCaptchaResponse(true))
		mockWebServer.enqueue(MockResponse()
			.setResponseCode(HttpStatus.OK.value())
			.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.setBody(response))

		val res = apiCallService.makeApiCall("lll")
		Assertions.assertEquals(true, res)

	}

}
