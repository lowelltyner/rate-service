package com.lowelltyner.rates.repository

import com.lowelltyner.rates.model.CurrentRate
import org.springframework.data.repository.CrudRepository
import java.time.DayOfWeek

interface RateRepository : CrudRepository<CurrentRate, Long> {

    fun findAllByOrigDay(day: DayOfWeek): List<CurrentRate>

}