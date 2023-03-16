package com.github.nayasis.excel

import com.github.nayasis.excel.implement.ApachePoiReader
import com.github.nayasis.excel.implement.ApachePoiWriter
import com.github.nayasis.kotlin.basica.core.io.extension
import com.github.nayasis.kotlin.basica.core.io.inputStream
import com.github.nayasis.kotlin.basica.core.io.outputStream
import com.github.nayasis.kotlin.basica.core.string.toPath
import com.github.nayasis.kotlin.basica.core.url.toInputStream
import com.github.nayasis.kotlin.basica.model.NGrid
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Path

private const val DEFAULT_SHEET = "Sheet1"

/**
 * Excel reader / writer
 */
class Excel {

    private var _path: Path?                 = null
    private var _resource: URL?              = null
    private var _inputstream: InputStream?   = null
    private var _outputstream: OutputStream? = null

    constructor(file: File) {
        _path = file.toPath()
    }

    constructor(path: Path) {
        this._path = path
    }

    constructor(path: String) {
        this._path = path.toPath()
    }

    constructor(resource: URL) {
        _resource = resource
    }

    constructor(instream: InputStream) {
        this._inputstream = instream
    }

    constructor(outstream: OutputStream) {
        this._outputstream = outstream
    }

    private fun inputstream(): InputStream {
        return when {
            _path != null -> _path!!.inputStream()
            _resource != null -> _resource!!.toInputStream()
            _inputstream != null -> _inputstream!!
            else -> throw IOException("No inputstream exists")
        }
    }

    private fun outputstream(): OutputStream {
        return when {
            _path != null -> _path!!.outputStream()
            _inputstream != null -> _outputstream!!
            else -> throw IOException("No outputstream exists")
        }
    }

    fun readAll(readHeader: Boolean = true): Map<String,NGrid> {
        try {
            return ApachePoiReader().readAll(inputstream(),readHeader)
        } finally {
            _inputstream = null
        }
    }

    fun read(readHeader: Boolean = true): NGrid {
        try {
            return ApachePoiReader().read(inputstream(),readHeader)
        } finally {
            _inputstream = null
        }
    }

    fun read(sheetName: String, readHeader: Boolean = true): NGrid {
        try {
            return ApachePoiReader().read(inputstream(),sheetName,readHeader)
        } finally {
            _inputstream = null
        }
    }

    fun writeAll(multiData: Map<String,NGrid>, readHeader: Boolean = true, fileType: ExcelType? = null) {
        try {
            ApachePoiWriter().write(outputstream(), multiData, getFileType(fileType), readHeader)
        } finally {
            _outputstream = null
        }
    }

    fun write(data: NGrid, sheetName: String = DEFAULT_SHEET, readHeader: Boolean = true, fileType: ExcelType? = null) {
        try {
            ApachePoiWriter().write(outputstream(), data, sheetName, getFileType(fileType), readHeader)
        } finally {
            _outputstream = null
        }
    }

    private fun getFileType(defaultType: ExcelType?): ExcelType {
        return when {
            defaultType != null -> defaultType
            _path != null       -> ExcelType.of(_path!!.extension)
            _resource != null   -> ExcelType.of(_resource.toString().substringAfterLast('.', ""))
            else                -> ExcelType.XLSX
        }
    }

}

