package com.chakaseptember.boardgameglossary.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.chakaseptember.boardgameglossary.database.Word
import com.chakaseptember.boardgameglossary.database.WordDatabase
import com.chakaseptember.boardgameglossary.network.WordNetwork
import com.chakaseptember.boardgameglossary.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordRepository(private val wordDatabase: WordDatabase) {
    val TAG: String = "word repo"
    var words: LiveData<List<Word>> = wordDatabase.wordDoa.getAllWords()

    private var wordDao = wordDatabase.wordDoa

    suspend fun refreshWords() {
        Log.d(TAG, "refreshing")
        withContext(Dispatchers.IO) {
            Log.d(TAG, "refreshing in coroutine")
            val wordList = WordNetwork.glossary.getGlossary().await()
            Log.d(TAG, wordList.words.toString())
            wordDao.insertAll(wordList.asDomainModel())
        }
    }

    fun getWordsForGame(game: String): LiveData<List<Word>> {
        words = wordDao.getAllWordsForGame(game)
        return words
    }

    fun getWordsForSearch(search: String): LiveData<List<Word>> {
        words = wordDao.getWordsForSearch("%" + search + "%")
        return words
    }


}