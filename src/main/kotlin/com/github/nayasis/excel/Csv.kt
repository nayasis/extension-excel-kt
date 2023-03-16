package com.github.nayasis.excel

import com.github.nayasis.excel.implement.OpenCsvReader
import com.github.nayasis.excel.implement.OpenCsvWriter
import com.github.nayasis.kotlin.basica.core.io.inputStream
import com.github.nayasis.kotlin.basica.core.io.outputStream
import com.github.nayasis.kotlin.basica.core.string.toPath
import com.github.nayasis.kotlin.basica.core.url.toInputStream
import com.github.nayasis.kotlin.basica.model.NGrid
import com.opencsv.CSVWriter
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.net.URL
import java.nio.file.Path

/**
 * CSV reader / writer
 */
class Csv {

    private var _path: Path?                 = null
    private var _resource: URL?              = null
    private var _inputstream: InputStream?   = null
    private var _outputstream: OutputStream? = null
    private var _reader: Reader?             = null
    private var _writer: Writer?             = null

    constructor(file: File) {
        _path = file.toPath()
    }

    constructor(path: Path) {
        _path = path
    }

    constructor(path: String) {
        _path = path.toPath()
    }

    constructor(resource: URL) {
        _resource = resource
    }

    constructor(inputStream: InputStream) {
        _inputstream = inputStream
    }

    constructor(outputStream: OutputStream) {
        _outputstream = outputStream
    }

    constructor(reader: Reader) {
        _reader = reader
    }

    constructor(writer: Writer) {
        _writer = writer
    }

    private fun reader(charset: String): Reader {
        return when {
            _reader != null -> _reader!!
            _inputstream != null -> InputStreamReader(_inputstream!!, charset)
            _path != null -> InputStreamReader(_path!!.inputStream(), charset)
            _resource != null -> InputStreamReader(_resource!!.toInputStream(), charset)
            else -> throw IOException("No reader exists")
        }
    }

    private fun writer(charset: String): Writer {
        return when {
            _writer != null -> _writer!!
            _outputstream != null -> OutputStreamWriter(_outputstream!!, charset)
            _path != null -> OutputStreamWriter(_path!!.outputStream(), charset)
            else -> throw IOException("No writer exists")
        }
    }

    fun read(readHeader: Boolean = true, charset: String = "UTF-8"): NGrid {
        try {
            return OpenCsvReader().read(reader(charset), readHeader)
        } finally {
            _reader = null
            _inputstream = null
        }
    }

     fun readlineAsMap(charset: String = "UTF-8", rowHandler: (row: Map<String,String>) -> Unit) {
        try {
            OpenCsvReader().read(reader(charset), rowHandler)
        } finally {
            _reader = null
            _inputstream = null
        }
    }

    fun readline(charset: String = "UTF-8", rowHandler: (row: Array<String>) -> Unit) {
        try {
            OpenCsvReader().read(reader(charset), rowHandler)
        } finally {
            _reader = null
            _inputstream = null
        }
    }

    fun write(data: NGrid, writeHeader: Boolean = true, charset: String = "UTF-8") {
        try {
            OpenCsvWriter().write(writer(charset), data, writeHeader)
        } catch (e: Exception) {
            _writer = null
            _outputstream = null
        }
    }

    fun write(charset: String = "UTF-8", handler: (csvWriter: CSVWriter) -> Unit) {
        try {
            OpenCsvWriter().write(writer(charset), handler)
        } catch (e: Exception) {
            _writer = null
            _outputstream = null
        }
    }

}