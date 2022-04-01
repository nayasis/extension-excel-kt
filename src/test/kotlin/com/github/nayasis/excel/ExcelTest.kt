package com.github.nayasis.excel

import com.github.nayasis.kotlin.basica.core.klass.Classes
import com.github.nayasis.kotlin.basica.core.path.Paths
import com.github.nayasis.kotlin.basica.core.path.delete
import com.github.nayasis.kotlin.basica.core.path.div
import com.github.nayasis.kotlin.basica.core.path.makeDir
import com.github.nayasis.kotlin.basica.model.NGrid
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

val log = KotlinLogging.logger{}

internal class ExcelTest {

    val TEST_DIR = Paths.userHome / "excel-test"

    @BeforeEach
    fun makeTemp() {
        TEST_DIR.makeDir()
    }

    @AfterEach
    fun clearTemp() {
        TEST_DIR.delete()
    }

    @Test
    fun `read & write`() {

        val file = TEST_DIR / "single.xlsx"

        val excel = Excel(file)

        excel.writeAll(testDatas())
        val sheets = excel.readAll()
        assertEquals( 3, sheets.size )

        for( sheet in sheets.values ) {
            assertTrue( sheet.header().containsKey("name") )
            assertTrue( sheet.header().containsKey("age") )
            assertTrue( sheet.header().containsKey("city") )
            assertEquals( 45L, sheet.getRow(0)["age"] )
            assertEquals( "jake", sheet.getRow(1)["name"] )
        }

    }

    @Test
    fun `read from resource`() {

        val excel = Excel(Classes.getResourceStream("/file/option.xlsx"))
        val sheet = excel.read()

        log.debug { "\n${sheet}" }

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