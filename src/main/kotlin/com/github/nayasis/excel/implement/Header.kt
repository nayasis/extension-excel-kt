package com.github.nayasis.excel.implement

data class Header(
    val body: LinkedHashMap<Int,String> = LinkedHashMap(),
    var has: Boolean = false,
)