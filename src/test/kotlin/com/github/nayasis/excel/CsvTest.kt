package com.github.nayasis.excel

import com.github.nayasis.kotlin.basica.core.io.Paths
import com.github.nayasis.kotlin.basica.core.io.delete
import com.github.nayasis.kotlin.basica.core.io.div
import com.github.nayasis.kotlin.basica.core.io.makeDir
import com.github.nayasis.kotlin.basica.core.string.toResource
import com.github.nayasis.kotlin.basica.core.url.detectCharset
import com.github.nayasis.kotlin.basica.model.NGrid
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger {}

class CsvTest {

    init {
        NGrid.fullFontWidth = 2.0
    }

    private val TEST_DIR = Paths.userHome / "csv-test"

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

        val file = TEST_DIR / "test.csv"

        val csv = Csv(file)
        csv.write(testdata)

        val data = csv.read()

        assertEquals("nayasis", data.getData(0, "name"))
        assertEquals("jake", data.getData(1, "name"))
        assertEquals("سلام", data.getData(2, "name"))
        assertEquals("nobody", data.getData(3, "name"))
        assertEquals("아무개", data.getData(4, "name"))
        assertEquals("任何", data.getData(5, "name"))
        assertEquals("どれか", data.getData(6, "name"))
        assertEquals("erklären", data.getData(7, "name"))
        assertEquals("Café", data.getData(8, "name"))

    }

    @Test
    fun `read & write with another charset`() {

        val file = TEST_DIR / "test.csv"

        val csv = Csv(file)
        csv.write(testdata, charset = "ms949")

        val data = csv.read(charset = "ms949")

        logger.debug { "\n${data}" }

        assertEquals("nayasis", data.getData(0, "name"))
        assertEquals("jake", data.getData(1, "name"))
        assertNotEquals("سلام", data.getData(2, "name"))
        assertEquals("nobody", data.getData(3, "name"))
        assertEquals("아무개", data.getData(4, "name"))
        assertEquals("任何", data.getData(5, "name"))
        assertEquals("どれか", data.getData(6, "name"))
        assertNotEquals("erklären", data.getData(7, "name"))
        assertNotEquals("Café", data.getData(8, "name"))

    }

    @Test
    fun `read from resource`() {
        val csv = Csv("/file/option.simple.csv".toResource()!!)
        val data = csv.read(readHeader = false)
        logger.debug { "\n${data}" }
        assertEquals(9, data.size)
        assertEquals(7, data.header.size)
    }

    @Test
    fun `charset encoding`() {
        val resource = "/file/charset.ms949.csv".toResource()!!
        val charset = resource.detectCharset()
        println(">> charset : $charset")
        Csv(resource).read(charset = "MS949").let {
            logger.debug { "\n${it}" }
            assertEquals("아무개", it.getData(1, "name"))
            assertEquals("nobody", it.getData(2, "name"))
            assertEquals("任何", it.getData(3, "name"))
            assertEquals("どれか", it.getData(4, "name"))
        }
    }

    @Test
    fun `readline by map rowHandler` () {

        val file = TEST_DIR / "test.csv"

        val csv = Csv(file)

        csv.write(testdata)

        var cnt = 0
        csv.readlineAsMap {
            cnt++
            println(it)
        }

        assertEquals(testdata.size, cnt)

    }

    @Test
    fun `readline by array rowHandler` () {

        val file = TEST_DIR / "test.csv"

        val csv = Csv(file)

        csv.write(testdata)

        var cnt = 0
        csv.readline {
            cnt++
            println(it.joinToString(","))
        }

        // regard head as line
        assertEquals(testdata.size + 1, cnt)

    }

    @Test
    fun `write line`() {

        val file = TEST_DIR / "test.csv"
        val csv = Csv(file)

        csv.write {writer ->
            testdata.forEach {
                writer.writeNext(it.values.map {"$it"}.toTypedArray())
            }
        }

        val written = csv.read(readHeader = false)
        logger.debug { "\n${written}" }

        assertEquals(testdata.size, written.size)

    }

}

val testdata: NGrid
    get() {
        return NGrid().apply {
            addRow(Person("nayasis", 45, "seoul"))
            addRow(Person("jake", 9, "sung-nam"))
            addRow(Person("سلام", 21, "busan"))
            addRow(Person("nobody", 33, "busan"))
            addRow(Person("아무개", 21, "busan"))
            addRow(Person("任何", 92, "beijing"))
            addRow(Person("どれか", 41, "Tokyo"))
            addRow(Person("erklären", 54, "Berlin"))
            addRow(Person("Café", 63, "Paris"))
        }
    }