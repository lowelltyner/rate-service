package com.lowelltyner.rates.model

import com.lowelltyner.rates.config.DefaultConstructor
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.DayOfWeek
import java.time.LocalTime

@DefaultConstructor
data class Rates(@Schema(description = "A collection of rates") val rates:List<Rate>)

@DefaultConstructor
data class Rate(
    @Schema(description = "Days of week rate is valid as a comma delimited abbreviated day list, e.g. 'mon,tues,wed'")
    val days:String,
    @Schema(description = "Time range that rate is valid in 'HHmm-HHmm' format")
    val times:String,
    @Schema(description = "Time zone as a TZ Identifier, e.g. 'America/Chicago'")
    val tz:String,
    @Schema(description = "Price associated with this rate's time range, e.g. 1500")
    val price:Int,
)

data class Price(@Schema(description = "Price of a rate") val price:Int)

@Entity
@DefaultConstructor
data class CurrentRate(
    val origDay: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val timeZone: String,
    val price: Int,
    @Id @GeneratedValue private val id: Long? = null
)