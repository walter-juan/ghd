package com.woowla.ghd.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

enum class City { SPAIN, FRANCE, GERMANY }
class EnumUnitTest : StringSpec({
    "enum value or default/else with null return the default value" {
        val expectedDefault = City.GERMANY
        val name: String? = null

        val orDefault = enumValueOfOrDefault(name, expectedDefault)
        val orElse = enumValueOfOrElse(name) { expectedDefault }

        orDefault shouldBe expectedDefault
        orElse shouldBe expectedDefault
    }

    "enum value or default/else with invalid string return the default value" {
        val expectedDefault = City.GERMANY
        val name: String = "this is an invalid string"

        val orDefault = enumValueOfOrDefault(name, expectedDefault)
        val orElse = enumValueOfOrElse(name) { expectedDefault }

        orDefault shouldBe expectedDefault
        orElse shouldBe expectedDefault
    }

    "enum value or default/else with valid string return the default value" {
        val expectedResult = City.SPAIN
        val expectedDefault = City.GERMANY
        val name: String = expectedResult.toString()

        val orDefault = enumValueOfOrDefault(name, expectedDefault)
        val orElse = enumValueOfOrElse(name) { expectedDefault }

        orDefault shouldBe expectedResult
        orElse shouldBe expectedResult
    }
})