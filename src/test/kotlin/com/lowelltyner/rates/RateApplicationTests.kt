package com.lowelltyner.rates

import com.lowelltyner.rates.controller.RateAndPriceController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RateApplicationTests {

	@Autowired
	private val ratesController: RateAndPriceController? = null

	@Test
	fun contextLoads() {
		assertThat(ratesController).isNotNull()
	}

}
