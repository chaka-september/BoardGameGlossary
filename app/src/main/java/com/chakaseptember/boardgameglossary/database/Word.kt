package com.chakaseptember.boardgameglossary.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "word_table", primaryKeys = arrayOf("word", "game"))
data class Word(
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "game") val game: String,
    @ColumnInfo(name = "expansion") val expansion: String
)