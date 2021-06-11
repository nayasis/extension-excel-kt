package com.github.nayasis.excel.implement

import com.github.nayasis.kotlin.basica.core.validator.isNotEmpty
import com.github.nayasis.kotlin.basica.core.validator.nvl
import com.github.nayasis.kotlin.basica.model.NGrid
import org.apache.poi.ss.format.CellDateFormatter
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import kotlin.math.max

class ApachePoiReader {

    fun read(instream: InputStream, readHeader: Boolean = true): NGrid {
        instream.use {
            WorkbookFactory.create(it).use { workbook ->
                return readSheet(workbook, getFirstSheetIndex(workbook), readHeader)
            }
        }
    }

    fun read(instream: InputStream, sheetName: String, readHeader: Boolean = true): NGrid {
        instream.use {
            WorkbookFactory.create(it).use { workbook ->
                return readSheet(workbook, getSheetNames(workbook)[sheetName] ?: -1, readHeader)
            }
        }
    }

    private fun getFirstSheetIndex(workbook: Workbook): Int {
        for( i in 0 until workbook.numberOfSheets ) {
            if( workbook.getSheetAt(i) != null ) return i
        }
        return -1
    }

    fun readAll(instream: InputStream, readHeader: Boolean): Map<String, NGrid> {
        val sheets = LinkedHashMap<String,NGrid>()
        instream.use {
            WorkbookFactory.create(it).use { workbook ->
                getSheetNames(workbook).forEach { (name, index) ->
                    sheets[name] = readSheet(workbook, index, readHeader)
                }
            }
        }
        return sheets
    }

    private fun getSheetNames(workbook: Workbook): Map<String, Int> {
        val names = LinkedHashMap<String,Int>()
        for( i in 0 until workbook.numberOfSheets ) {
            names[workbook.getSheetName(i)] = i
        }
        return names
    }

    private fun readSheet(workbook: Workbook, index: Int, readHeader: Boolean): NGrid {

        val rs     = NGrid().also { if(index < 0) return it }
        val sheet  = workbook.getSheetAt(index).also { if(it==null) return rs }
        val header = getHeader(sheet,readHeader)

        rs.header().addAll(header.body.values.toSet())

        val formula = workbook.creationHelper.createFormulaEvaluator()

        for( i in (if(header.has) 1 else 0) until sheet.physicalNumberOfRows ) {
            val data = LinkedHashMap<String,Any?>()
            val row  = sheet.getRow(i)
            for( c in 0 until header.body.size) {
                val cell = row.getCell(c) ?: continue
                val key  = nvl(header.body[c], c).toString()
                data[key] = getValue(cell,formula)
            }
            rs.addRow(data)
        }

        return rs

    }

    private fun getValue(cell: Cell, formula: FormulaEvaluator): Any {
        return when(cell.cellType) {
            CellType.FORMULA -> {
                when {
                    isNotEmpty(cell) -> {
                        when(formula.evaluateFormulaCell(cell)) {
                            CellType.NUMERIC -> getNumericValue(cell)
                            CellType.BOOLEAN -> cell.booleanCellValue
                            else -> cell.stringCellValue
                        }
                    }
                    else -> cell.stringCellValue
                }
            }
            CellType.NUMERIC -> getNumericValue(cell)
            CellType.BOOLEAN -> cell.booleanCellValue
            else -> cell.stringCellValue
        }
    }

    private fun getHeader(sheet: Sheet, readHeader: Boolean): Header {

        val header = Header()
        val count  = getColumnCount(sheet).also { if(it==0) return header }

        sheet.getRow(0)?.let{ row ->
            try {
                for( i in 0 until count )
                    header.body[i] = if(readHeader) row.getCell(i).stringCellValue else nvl(i)
                header.has = true
            } catch (e: NullPointerException) {
                header.body.clear()
                for( i in 0 until count )
                    header.body[i] = nvl(i)
            }
        }

        return header

    }

    private fun getColumnCount(sheet: Sheet): Int {
        var max = 0
        for( i in 0 until sheet.physicalNumberOfRows ) {
            max = max( max, sheet.getRow(i).lastCellNum.toInt() )
        }
        return max
    }

    private fun getNumericValue(cell: Cell): Any {
        val value = cell.numericCellValue
        return if( isDateFormatted(cell) ) {
            val format = cell.cellStyle.dataFormatString
            CellDateFormatter(format).format(DateUtil.getJavaDate(value))
        } else {
            val fixedVal = value.toLong()
            if( value % 1 == 0.0 ) {
                fixedVal
            } else {
                cell.numericCellValue
            }
        }
    }


    private fun isDateFormatted( cell: Cell? ): Boolean {

        if( cell == null || ! DateUtil.isValidExcelDate(cell.numericCellValue) ) return false

        val style = cell.cellStyle.also { if(it==null) return false }

        val formatIndex = style.dataFormat.toInt()
        val format      = style.dataFormatString.replace( "([^\\\\])\".*?[^\\\\]\"".toRegex(), "$1" );

        return DateUtil.isADateFormat(formatIndex,format)

    }

}

data class Header(
    val body: LinkedHashMap<Int,String> = LinkedHashMap(),
    var has: Boolean = false,
)