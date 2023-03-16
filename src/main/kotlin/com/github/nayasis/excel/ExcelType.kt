package com.github.nayasis.excel

enum class ExcelType(val cd: String) {

    XLS("xls"),
    XLSX("xlsx"),
    ;

    companion object {
        @JvmStatic
        fun of(code: String?): ExcelType {
            if( code.isNullOrEmpty() ) return XLSX;
            return values().firstOrNull { e -> e.cd == code } ?: XLSX
        }
    }

}

