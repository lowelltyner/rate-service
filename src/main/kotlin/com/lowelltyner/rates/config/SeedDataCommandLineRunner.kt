package com.lowelltyner.rates.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.lowelltyner.rates.model.Rates
import com.lowelltyner.rates.service.RateAndPriceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.File

@Component
class SeedDataCommandLineRunner(private val rapService: RateAndPriceService) : CommandLineRunner{

    private val logger: Logger = LoggerFactory.getLogger(SeedDataCommandLineRunner::class.java)

    override fun run(vararg args: String?) {
        logger.info("Seeding supplied or default rate data")
        val objectMapper = ObjectMapper()
        val dir = File(System.getProperty("user.dir"))
        logger.info("Looking for user supplied rates file in :" + dir.absolutePath);
        val userJsonFile = dir.listFiles { _, n -> n.endsWith("rate.json", true) }?.firstOrNull()
        if (userJsonFile == null) {
            logger.info("No user supplied rate.json file found, loading default seed data")
        }
        val jsonStream = userJsonFile?.inputStream() ?: ClassPathResource("seedData.json").inputStream
        // Parse JSON file
        jsonStream.use {
            rapService.saveRates(objectMapper.readValue(it, Rates::class.java).rates)
        }
        logger.info("${rapService.getRates().size} rates initially stored")
    }
}