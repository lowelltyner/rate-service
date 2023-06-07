package com.lowelltyner.rates.controller

import com.lowelltyner.rates.model.CurrentRate
import com.lowelltyner.rates.model.Price
import com.lowelltyner.rates.model.Rates
import com.lowelltyner.rates.service.RateAndPriceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RateAndPriceController(private val rapService: RateAndPriceService) {

    @GetMapping("/rates")
    @Operation(description = "Provides all current rates")
    fun getRates(): List<CurrentRate> {
        return rapService.getRates()
    }

    @PutMapping("/rates")
    @Operation(description = "Replaces current rates with updated rate data")
    fun setRates(@RequestBody newRates: Rates) {
        rapService.saveRates(newRates.rates)
    }

    @GetMapping("/price")
    @Operation(description = "Provides the price at a given time range, given start and end times")
    fun getPrice(@RequestParam @Parameter(description = "ISO-8601 with timezone") startTime:String,
                 @RequestParam @Parameter(description = "ISO-8601 with timezone") endTime:String): Price {
        return Price(rapService.getPriceForRange(startTime, endTime))
    }
}