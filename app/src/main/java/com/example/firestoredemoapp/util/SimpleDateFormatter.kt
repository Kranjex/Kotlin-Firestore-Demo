package com.example.firestoredemoapp.util

import java.text.SimpleDateFormat
import java.util.Date

object SimpleDateFormatter {
    private val SDF = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

    fun createNewDate(): String = SDF.format(Date())
}