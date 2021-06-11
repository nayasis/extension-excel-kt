package com.github.nayasis.excel

import com.github.nayasis.kotlin.basica.core.path.delete
import com.github.nayasis.kotlin.basica.core.path.div
import com.github.nayasis.kotlin.basica.core.path.makeDir
import com.github.nayasis.kotlin.basica.core.path.userHome
import com.github.nayasis.kotlin.basica.model.NGrid
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

val log = KotlinLogging.logger{}

internal class ExcelTest {

    val TEST_DIR = userHome() / "excel-test"

    @BeforeEach
    fun makeTemp() {
        TEST_DIR.makeDir()
    }

    @AfterEach
    fun clearTemp() {
        TEST_DIR.delete()
    }

    @Test
    fun `single`() {

        val file = TEST_DIR / "single.xlsx"

        val excel = Excel(file)

        excel.writeAll(testDatas())
        val sheets = excel.readAll()
        val first  = sheets["A"]!!


        log.debug { "\n${first}" }

        assertEquals( 3, sheets.size )
        assertEquals( "[name, age, city]", first.header().keys().toString() )
        assertEquals( 45, first.getRow(0)["age"] )
        assertEquals( "jake", first.getRow(1)["name"] )

    }

    private fun testData(): NGrid {
        return NGrid().apply {
            addRow(Person("nayasis", 45, "seoul"))
            addRow(Person("jake", 9, "sung-nam"))
        }
    }

    private fun testDatas(): Map<String,NGrid> {
        return mapOf(
            "A" to testData(),
            "B" to testData(),
            "C" to testData(),
        )
    }

}

data class Person(
    val name: String,
    val age: Int,
    val city: String,
)