package com.lowelltyner.rates.controller

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RateAndPriceControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun userEnteredRangeThatMatchesDefaultRangeWithPriceOf350() {
        val result = mockMvc.perform(get("/price")
            .queryParam("startTime", "2015-07-02T07:00:00-05:00")
            .queryParam("endTime", "2015-07-02T12:00:00-05:00"))
            .andExpect(status().isOk)
            .andReturn()
        val response = JSONObject(result.response.contentAsString)
        assertThat(response.has("price")).isTrue()
        assertThat(response.get("price")).isEqualTo(350)
    }

    @Test
    fun suppliedRateDataGetsSaved() {
        val jsonString = """
        {
            "rates": [
                {
                    "days": "mon,wed,fri",
                    "times": "0100-2100",
                    "tz": "America/Chicago",
                    "price": 1500
                }
            ]
        }"""
        mockMvc.perform(put("/rates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isOk)
        val response = JSONArray(mockMvc.get("/rates").andReturn().response.contentAsString)
        assertThat(response.length()).isEqualTo(3)
        assertThat(response.getJSONObject(0).has("origDay"))
    }

    @Test
    fun suppliedBadRateDataGetsError() {
        val jsonString = """
        {
            "rates": [
                {
                    "days": "mon,wed,fri",
                    "times": "01AB-2100",
                    "tz": "America/Chicago",
                    "price": 1500
                }
            ]
        }"""
        val response = mockMvc.perform(put("/rates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().is4xxClientError)
            .andReturn().response.contentAsString
        assertThat(response).isEqualTo("unavailable")
    }

    @Test
    fun requestedRangeThatMatchesDefaultRangeWithPriceOf85() {
        val result = mockMvc.perform(get("/price")
            .queryParam("startTime", "2015-07-04T15:00:00+00:00")
            .queryParam("endTime", "2015-07-04T20:00:00+00:00"))
            .andExpect(status().isOk)
            .andReturn()
        val response = JSONObject(result.response.contentAsString)
        assertThat(response.has("price")).isTrue()
        assertThat(response.get("price")).isEqualTo(85)
    }

    @Test
    fun requestedRangeDoesNotMatchResultsInUnavailable() {
        val result = mockMvc.perform(get("/price")
            .queryParam("startTime", "2015-07-04T07:00:00+05:00")
            .queryParam("endTime", "2015-07-04T20:00:00+05:00"))
            .andExpect(status().is4xxClientError)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo("unavailable")
    }

}
