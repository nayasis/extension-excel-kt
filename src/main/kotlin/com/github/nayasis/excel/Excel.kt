package com.github.nayasis.excel

import com.github.nayasis.excel.implement.ApachePoiReader
import com.github.nayasis.excel.implement.ApachePoiWriter
import com.github.nayasis.kotlin.basica.core.path.extension
import com.github.nayasis.kotlin.basica.core.path.inStream
import com.github.nayasis.kotlin.basica.core.path.outStream
import com.github.nayasis.kotlin.basica.core.string.toPath
import com.github.nayasis.kotlin.basica.model.NGrid
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

private const val DEFAULT_SHEET = "Sheet1"

class Excel {

    var fileType = "xlsx"

    private val reader = ApachePoiReader()
    private val writer = ApachePoiWriter()

    private var path: Path?              = null
    private var instream: InputStream?   = null
    private var outstream: OutputStream? = null

    constructor(file: File) {
        path = file.toPath()
        fileType = file.extension
    }

    constructor(path: Path) {
        this.path = path
        fileType = path.extension
    }

    constructor(path: String) {
        this.path = path.toPath()
        fileType = this.path!!.extension
    }

    constructor(instream: InputStream) {
        this.instream = instream
    }

    constructor(outstream: OutputStream) {
        this.outstream = outstream
    }

    fun instream(instream: InputStream) {
        this.instream = instream
    }

    fun outtream(outstream: OutputStream) {
        this.outstream = outstream
    }

    private fun instream(): InputStream {
        return when {
            path != null -> path!!.inStream()
            instream != null -> instream!!
            else -> throw IllegalArgumentException("no resource to read")
        }
    }

    private fun outstream(): OutputStream {
        return when {
            path != null -> path!!.outStream()
            instream != null -> outstream!!
            else -> throw IllegalArgumentException("no resource to write")
        }
    }

    fun readAll(readHeader: Boolean = true): Map<String,NGrid> {
        return reader.readAll(instream(),readHeader)
    }

    fun read(readHeader: Boolean = true): NGrid {
        return reader.read(instream(),readHeader)
    }

    fun read(sheetName: String, readHeader: Boolean = true): NGrid {
        return reader.read(instream(),sheetName,readHeader)
    }

    fun writeAll(datas: Map<String,NGrid>, readHeader: Boolean = true) {
        writer.write(outstream(),datas,fileType,readHeader)
    }

    fun write(data: NGrid, sheetName: String = DEFAULT_SHEET, readHeader: Boolean = true) {
        writer.write(outstream(),data,sheetName,fileType,readHeader)
    }

}

