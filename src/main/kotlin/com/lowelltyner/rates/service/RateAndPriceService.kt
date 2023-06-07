package com.lowelltyner.rates.service

import com.lowelltyner.rates.model.CurrentRate
import com.lowelltyner.rates.model.Rate
import com.lowelltyner.rates.repository.RateRepository
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId

private val DAY_ABBREV = listOf("mon", "tues", "wed", "thurs", "fri", "sat", "sun")

private val WEEK_DAYS = mapOf(
    "mon" to DayOfWeek.MONDAY, "tues" to DayOfWeek.TUESDAY,
    "wed" to DayOfWeek.WEDNESDAY, "thurs" to DayOfWeek.THURSDAY,
    "fri" to DayOfWeek.FRIDAY, "sat" to DayOfWeek.SATURDAY,
    "sun" to DayOfWeek.SUNDAY
)

@Service
class RateAndPriceService(private val rateRepo: RateRepository) {

    private val logger: Logger = LoggerFactory.getLogger(RateAndPriceService::class.java)

    fun getRates(): List<CurrentRate> {
        return rateRepo.findAll().toList()
    }

    @Transactional
    fun saveRates(rates: List<Rate>) {
        val curRates = mutableListOf<CurrentRate>()
        for (rate in rates) {
            // rate.days = "sat,sun,mon"
            val days = rate.days.split(",")
            for (day in days) {
                val times = rate.times.split("-").map { getTime(it) }
                if (!times[0].isBefore(times[1])) {
                    logger.warn("Date range invalid, skipping current rate")
                    continue
                }
                val fuzzyDay = FuzzySearch.extractOne(day, DAY_ABBREV).string
                curRates.add(CurrentRate(WEEK_DAYS[fuzzyDay]!!, times[0], times[1], rate.tz, rate.price))
            }
        }
        rateRepo.deleteAll()
        rateRepo.saveAll(curRates)
    }

    fun getPriceForRange(start: String, end: String): Int {
        val startTime = OffsetDateTime.parse(start)
        val endTime = OffsetDateTime.parse(end)
        if (!startTime.toLocalDate().equals(endTime.toLocalDate()) || endTime.isBefore(startTime)) {
            throw IllegalArgumentException("Input date range invalid for: $start / $end")
        }
        val dayRates = rateRepo.findAllByOrigDay(startTime.dayOfWeek)
        return dayRates.stream()
            .filter {
                rangeMatches(it, startTime, endTime)
            }.findFirst()
            .map {
                it.price
            }.orElseThrow { IllegalArgumentException("No date range found matching: $start / $end") }
    }

    private fun rangeMatches(rate: CurrentRate, startTime: OffsetDateTime, endTime: OffsetDateTime): Boolean {
        val zone = ZoneId.of(rate.timeZone)
        val comp1 = startTime.atZoneSameInstant(zone).toLocalTime()
        val comp2 = endTime.atZoneSameInstant(zone).toLocalTime()
        return if (comp2.isBefore(comp1)) {
            // Spans days in the original timezone, which we are preventing upon save
            false
        } else {
            // The rate fully encapsulates the user entered range
            !comp1.isBefore(rate.startTime) && !comp2.isAfter(rate.endTime)
        }
    }

    private fun getTime(time: String): LocalTime {
        // expected time format: 0700
        val hour = time.substring(0, 2).toInt()
        val minute = time.substring(2, 4).toInt()
        return LocalTime.of(hour, minute)
    }

}
