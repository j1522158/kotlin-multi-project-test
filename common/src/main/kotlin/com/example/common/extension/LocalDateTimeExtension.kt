package com.example.common.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日時型を指定フォーマットの文字列型に変換する
 */
fun LocalDateTime.toFormatString(format: String): String = DateTimeFormatter.ofPattern(format).format(this)