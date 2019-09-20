package com.chakaseptember.boardgameglossary.network

import com.chakaseptember.boardgameglossary.database.Word
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GlossaryContainer(val words: List<WordDto>)

data class WordDto(
    val word: String,
    val description: String,
    val game: String,
    val expansion: String
)

fun GlossaryContainer.asDomainModel(): List<Word> {
    return words.map { Word(it.word, it.description, it.game, it.expansion) }
}