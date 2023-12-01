package com.github.nayasis.excel

import com.github.nayasis.kotlin.basica.core.io.Paths
import com.github.nayasis.kotlin.basica.core.io.delete
import com.github.nayasis.kotlin.basica.core.io.div
import com.github.nayasis.kotlin.basica.core.io.makeDir
import com.github.nayasis.kotlin.basica.core.string.toResource
import com.github.nayasis.kotlin.basica.model.NGrid
import com.github.nayasis.kotlin.basica.reflection.Reflector
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger{}

internal class ExcelTest {

    init {
        NGrid.fullFontWidth = 2.0
    }

    private val TEST_DIR = Paths.userHome / "excel-test"

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

        val excel  = Excel(file).apply { writeAll(dataMultisheet()) }
        val sheets = excel.readAll()
        assertEquals( 3, sheets.size )

        for( sheet in sheets.values ) {
            assertTrue( sheet.header.containsKey("name") )
            assertTrue( sheet.header.containsKey("age") )
            assertTrue( sheet.header.containsKey("city") )
            assertEquals( 45L, sheet.getRow(0)["age"] )
            assertEquals( "jake", sheet.getRow(1)["name"] )
        }

    }

    @Test
    fun `read from resource`() {
        val excel = Excel("/file/option.xlsx".toResource()!!)
        val sheet = excel.read()
        logger.debug { "\n${sheet}" }
    }

    private fun dataSingleSheet(): NGrid {
        return NGrid().apply {
            addRow(Person("nayasis", 45, "seoul"))
            addRow(Person("jake", 9, "sung-nam"))
        }
    }

    private fun dataMultisheet(): Map<String,NGrid> {
        return mapOf(
            "A" to dataSingleSheet(),
            "B" to dataSingleSheet(),
            "C" to dataSingleSheet(),
        )
    }

    @Test
    fun `charset encoding`() {
        val excel = Excel("/file/charset.xlsx".toResource()!!)
        val sheet = excel.read()
        logger.debug { "\n${sheet.toString()}" }
        assertEquals("سلام", sheet.getData(0,"name"))
        assertEquals("아무개", sheet.getData(1,"name"))
        assertEquals("nobody", sheet.getData(2,"name"))
        assertEquals("任何", sheet.getData(3,"name"))
        assertEquals("どれか", sheet.getData(4,"name"))
        assertEquals("erklären", sheet.getData(5,"name"))
        assertEquals("Café", sheet.getData(6,"name"))
    }

    @Test
    fun `write emuloader config`() {
        val options = toNGrid(Reflector.toObject<List<Option>>(testjson))
        val fileTmp = TEST_DIR / "test.xlsx"
        Excel(fileTmp).write(options)
        Excel(fileTmp).read().toString(true).let { contents ->
            println(contents)
            assertTrue(contents.isNotEmpty())
        }
    }

    fun toNGrid(options: List<Option>): NGrid {
        return NGrid().apply {

            setCell(0,0,"item")
            setCell(1,0,"caption")
            setCell(2,0,"show")
            setCell(3,0,"key")
            setCell(4,0,"desc")
            setCell(5,0,"default value")
            setCell(6,0,"type")

            options.forEachIndexed { i, option ->
                val c0 = 1 + i * 3
                val c1 = 2 + i * 3
                val c2 = 3 + i * 3

                setCell(0,c0,option.key)
                setCell(1,c0,option.caption)
                setCell(2,c0,true)
                setCell(4,c0,"")
                setCell(5,c0,option.defaultVal)
                setCell(6,c0,"combo")
                setCell(6,c1,"label")
                setCell(6,c2,"value")

                option.items.forEachIndexed { row, item ->
                    setCell( 7 + row, c1, item.label )
                    setCell( 7 + row, c2, item.value )
                }
            }

        }
    }

}

data class Person(
    val name: String,
    val age: Int,
    val city: String,
)

data class Option(
    var caption: String,
    var key: String,
    var defaultVal: String? = null,
    val items: MutableList<Item> = mutableListOf(),
) {
    class Item(
        var label: String = "",
        var value: String = "",
    )
}

val testjson = """
[ {
  "caption" : "Force HLE BIOS (restart)",
  "key" : "kronos_force_hle_bios",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Video format",
  "key" : "kronos_videoformattype",
  "defaultVal" : "auto",
  "items" : [ {
    "label" : "auto",
    "value" : "auto"
  }, {
    "label" : "NTSC",
    "value" : "NTSC"
  }, {
    "label" : "PAL",
    "value" : "PAL"
  } ]
}, {
  "caption" : "Frameskip",
  "key" : "kronos_skipframe",
  "defaultVal" : "0",
  "items" : [ {
    "label" : "No skipping",
    "value" : "0"
  }, {
    "label" : "Skip rendering of 1 frames out of 2",
    "value" : "1"
  }, {
    "label" : "Skip rendering of 2 frames out of 3",
    "value" : "2"
  }, {
    "label" : "Skip rendering of 3 frames out of 4",
    "value" : "3"
  }, {
    "label" : "Skip rendering of 4 frames out of 5",
    "value" : "4"
  }, {
    "label" : "Skip rendering of 5 frames out of 6",
    "value" : "5"
  } ]
}, {
  "caption" : "SH-2 cpu core",
  "key" : "kronos_sh2coretype",
  "defaultVal" : "kronos",
  "items" : [ {
    "label" : "kronos",
    "value" : "kronos"
  }, {
    "label" : "old",
    "value" : "old"
  } ]
}, {
  "caption" : "Video renderer",
  "key" : "kronos_videocoretype",
  "defaultVal" : "opengl",
  "items" : [ {
    "label" : "OpenGL (requires OpenGL 4.2+)",
    "value" : "opengl"
  }, {
    "label" : "OpenGL CS (requires OpenGL 4.3+)",
    "value" : "opengl_cs"
  } ]
}, {
  "caption" : "Share saves with beetle",
  "key" : "kronos_use_beetle_saves",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Addon Cartridge (restart)",
  "key" : "kronos_addon_cartridge",
  "defaultVal" : "512K_backup_ram",
  "items" : [ {
    "label" : "None",
    "value" : "None"
  }, {
    "label" : "1M_extended_ram",
    "value" : "1M_extended_ram"
  }, {
    "label" : "4M_extended_ram",
    "value" : "4M_extended_ram"
  }, {
    "label" : "16M_extended_ram",
    "value" : "16M_extended_ram"
  }, {
    "label" : "512K_backup_ram",
    "value" : "512K_backup_ram"
  }, {
    "label" : "1M_backup_ram",
    "value" : "1M_backup_ram"
  }, {
    "label" : "2M_backup_ram",
    "value" : "2M_backup_ram"
  }, {
    "label" : "4M_backup_ram",
    "value" : "4M_backup_ram"
  } ]
}, {
  "caption" : "6Player Adaptor on Port 1",
  "key" : "kronos_multitap_port1",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "6Player Adaptor on Port 2",
  "key" : "kronos_multitap_port2",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Resolution",
  "key" : "kronos_resolution_mode",
  "defaultVal" : "original",
  "items" : [ {
    "label" : "original",
    "value" : "original"
  }, {
    "label" : "480p",
    "value" : "480p"
  }, {
    "label" : "720p",
    "value" : "720p"
  }, {
    "label" : "1080p",
    "value" : "1080p"
  }, {
    "label" : "4k",
    "value" : "4k"
  }, {
    "label" : "8k",
    "value" : "8k"
  } ]
}, {
  "caption" : "Output to original resolution",
  "key" : "kronos_force_downsampling",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Polygon Mode",
  "key" : "kronos_polygon_mode",
  "defaultVal" : "cpu_tesselation",
  "items" : [ {
    "label" : "perspective_correction",
    "value" : "perspective_correction"
  }, {
    "label" : "gpu_tesselation",
    "value" : "gpu_tesselation"
  }, {
    "label" : "cpu_tesselation",
    "value" : "cpu_tesselation"
  } ]
}, {
  "caption" : "Improved mesh",
  "key" : "kronos_meshmode",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Improved banding",
  "key" : "kronos_bandingmode",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "RGB Compute shaders",
  "key" : "kronos_use_cs",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "Wireframe mode",
  "key" : "kronos_wireframe_mode",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "ST-V Service/Test Buttons",
  "key" : "kronos_service_enabled",
  "defaultVal" : "disabled",
  "items" : [ {
    "label" : "Disabled",
    "value" : "disabled"
  }, {
    "label" : "Enabled",
    "value" : "enabled"
  } ]
}, {
  "caption" : "ST-V Favorite Region",
  "key" : "kronos_stv_favorite_region",
  "defaultVal" : "EU",
  "items" : [ {
    "label" : "EU",
    "value" : "EU"
  }, {
    "label" : "US",
    "value" : "US"
  }, {
    "label" : "JP",
    "value" : "JP"
  }, {
    "label" : "TW",
    "value" : "TW"
  } ]
}, {
  "caption" : "Bios Language",
  "key" : "kronos_language_id",
  "defaultVal" : "English",
  "items" : [ {
    "label" : "English",
    "value" : "English"
  }, {
    "label" : "German",
    "value" : "German"
  }, {
    "label" : "French",
    "value" : "French"
  }, {
    "label" : "Spanish",
    "value" : "Spanish"
  }, {
    "label" : "Italian",
    "value" : "Italian"
  }, {
    "label" : "Japanese",
    "value" : "Japanese"
  } ]
} ]
""".trimIndent()