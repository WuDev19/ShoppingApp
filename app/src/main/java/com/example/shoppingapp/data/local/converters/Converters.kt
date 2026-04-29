package com.example.shoppingapp.data.local.converters

import androidx.room.TypeConverter

object Converters {

    @TypeConverter
    fun fromSetToString(set: Set<Int>): String = set.joinToString(",")


    @TypeConverter
    fun fromStringToSet(string: String): Set<Int> =
        if (string.isEmpty()) emptySet()
        else string
            .split(",")
            .map { str -> str.toInt() }
            .toSet()

}