package com.github.nayasis.excel.implement

import com.github.nayasis.kotlin.basica.model.NGrid
import com.opencsv.CSVReader
import java.io.Reader

class OpenCsvReader{

    fun read(reader: Reader, readHeader: Boolean = true): NGrid {
        val rs = NGrid()
        CSVReader(reader).use { csvReader ->
            val header = getHeader(csvReader, readHeader)
            rs.header.addAll(header.body.values.toSet())
            if(header.has) {
                csvReader.skip(1)
            }
            csvReader.forEachIndexed{ i, row ->
                row.forEachIndexed { j, cell ->
                    rs.setCell(i,j,cell)
                }
            }
        }
        return rs
    }

    @JvmName("readAsMap")
    fun read(reader: Reader, rowHandler: (row: Map<String,String>) -> Unit) {
        CSVReader(reader).use { csvReader ->
            val header = getHeader(csvReader, true)
            if(header.has) {
                csvReader.skip(1)
            }
            csvReader.forEach {
                val row = it.mapIndexed { i, value -> header.body[i]!! to value }.toMap()
                rowHandler.invoke(row)
            }
        }
    }

    fun read(reader: Reader, rowHandler: (row: Array<String>) -> Unit) {
        CSVReader(reader).use { csvReader ->
            csvReader.forEach {
                rowHandler.invoke(it)
            }
        }
    }

    private fun getHeader(csvReader: CSVReader, readHeader: Boolean): Header {
        val firstRow = csvReader.peek().also { if(it.isEmpty()) return Header() }
        return Header(has = readHeader).apply {
            firstRow.forEachIndexed { i, cell ->
                body[i] = if(readHeader) cell else "$i"
            }
        }
    }

}