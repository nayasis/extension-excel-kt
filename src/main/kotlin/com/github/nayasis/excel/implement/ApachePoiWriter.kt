package com.github.nayasis.excel.implement

import com.github.nayasis.kotlin.basica.core.validator.nvl
import com.github.nayasis.kotlin.basica.model.NGrid
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.*
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType.*
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

private const val MAX_TEXT_LENGTH = 32_707

class ApachePoiWriter {

    fun write(outstream: OutputStream, data: Map<String,NGrid>, fileType: String, writeHeader: Boolean = true ) {
        val workbook = createWorkbook(fileType)
        data.forEach { (name, data) -> writeSheet(workbook,name,data,writeHeader) }
        outstream.use { workbook.write(outstream) }
    }

    fun write(outstream: OutputStream, data: NGrid, sheetName: String, fileType: String, writeHeader: Boolean = true ) {
        val workbook = createWorkbook(fileType)
        writeSheet(workbook,sheetName,data,writeHeader)
        outstream.use { workbook.write(outstream) }
    }

    private fun createWorkbook(type: String): Workbook {
        return when(type.lowercase()) {
            "xlsx" -> XSSFWorkbook()
            "xls" -> HSSFWorkbook()
            else -> XSSFWorkbook()
        }
    }

    private fun writeSheet(workbook: Workbook, sheetName: String, data: NGrid, writeHeader: Boolean) {

        var r = 0; var c = 0

        val sheet = workbook.createSheet(sheetName)

        if( writeHeader ) {
            val row   = sheet.createRow(r++)
            val style = getHeaderStyle(workbook)
            for( alias in data.header.aliases() ) {
                row.createCell(c++).apply {
                    setCellValue(alias)
                    cellStyle = style
                }
            }
        }

        for( map in data ) {
            c = 0
            val row = sheet.createRow(r++)
            for( key in data.header.keys() ) {
                when(val value = map[key]) {
                    null -> row.createCell(c++)
                    is Number -> row.createCell(c++, NUMERIC).setCellValue(value.toDouble())
                    is Boolean -> row.createCell(c++, BOOLEAN).setCellValue(value)
                    else -> row.createCell(c++, STRING).setCellValue(toText(value))
                }
            }
        }


    }

    private fun toText(obj: Any?): String {
        return nvl(obj).let { if(it.length > MAX_TEXT_LENGTH) it.substring(0, MAX_TEXT_LENGTH) else it }
    }

    private fun getHeaderStyle(workbook: Workbook): CellStyle {
        return workbook.createCellStyle().apply {
            fillBackgroundColor = GREY_40_PERCENT.index
            setFont(workbook.createFont().apply { bold = true })
        }
    }

}

