package com.lowelltyner.rates.service

import com.lowelltyner.rates.model.Rate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.time.DayOfWeek
import java.time.LocalTime

const val DEFAULT_SEED_DATA_SIZE = 10
const val TEST_TZ = "America/Chicago"

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RateAndPriceServiceTests {

    @Autowired
    private lateinit var rapService: RateAndPriceService

    @Test
    fun defaultSeededDataIsPresent() {
        assertThat(rapService.getRates().size).isEqualTo(DEFAULT_SEED_DATA_SIZE)
    }

    @Test
    fun providedDataReplacesExistingDataAndIsCorrect() {
        val price = 1200
        val list = listOf(Rate("mon", "1000-1400", TEST_TZ, price))
        rapService.saveRates(list)
        val saved = rapService.getRates()
        assertThat(saved.size).isEqualTo(1)
        assertThat(saved[0].origDay).isEqualTo(DayOfWeek.MONDAY)
        assertThat(saved[0].startTime).isEqualTo(LocalTime.of(10,0))
        assertThat(saved[0].endTime).isEqualTo(LocalTime.of(14, 0))
        assertThat(saved[0].timeZone).isEqualTo(TEST_TZ)
        assertThat(saved[0].price).isEqualTo(price)
    }

    @Test
    fun malformedDataDoesNotClearExistingData() {
        val malformed = listOf(Rate("mon,sat", "ABCD-0200", TEST_TZ, 800))
        runCatching { rapService.saveRates(malformed) }
        assertThat(rapService.getRates().size).isEqualTo(DEFAULT_SEED_DATA_SIZE)
    }

    @Test
    fun timeRangeSpanningDaysDoesNotGetSaved() {
        val span = listOf(Rate("mon,tues", "2130-0300", "America/Chicago", 1700))
        rapService.saveRates(span)
        assertThat(rapService.getRates().size).isEqualTo(0)
    }

    @Test
    fun priceEncapsulatedInRangeReturnsCorrectPrice() {
        val start = "2015-07-02T07:00:00-05:00"
        val end = "2015-07-02T12:00:00-05:00"
        assertThat(rapService.getPriceForRange(start, end)).isEqualTo(350)
    }

    @Test
    fun priceThatExtendsDefinedRateReturnsUnavailable() {
        // It was a Wednesday
        val start = "2015-07-01T07:00:00-05:00"
        // 1 minute past default data range
        val end = "2015-07-01T18:01:00-05:00"
        assertThrows<IllegalArgumentException> { rapService.getPriceForRange(start, end) }
    }

}