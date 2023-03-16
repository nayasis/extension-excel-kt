package com.github.nayasis.excel.implement

import com.github.nayasis.kotlin.basica.model.NGrid
import com.opencsv.CSVWriter
import java.io.Writer

class OpenCsvWriter{

    fun write(writer: Writer, data: NGrid, writeHeader: Boolean = true) {
        CSVWriter(writer).use{ csvWriter ->
            if(writeHeader) {
                csvWriter.writeNext(data.header.aliases().map {"$it"}.toTypedArray())
            }
            val keys = data.header.keys()
            data.body.forEach {
                val row = it.value
                csvWriter.writeNext(keys.map { "${row[it]}" }.toTypedArray())
            }
        }
    }

    fun write(writer: Writer, handler: (csvWriter: CSVWriter) -> Unit ) {
        CSVWriter(writer).use { csvWriter ->
            handler.invoke(csvWriter)
        }
    }

}