package com.lynas.hcaptchaspringmvc

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
class HcaptchaSpringMvcApplicationTests {


	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var webApplicationContext: WebApplicationContext

	@BeforeEach
	fun setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
	}

	@Test
	fun contextLoads() {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string("Hello World!"))
	}

}
