package com.chakaseptember.boardgameglossary.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDao {

    @Insert
    fun insert(word: Word)

    @Update
    fun update(word: Word)

    @Delete
    fun delete(word: Word)

    @Query("SELECT * FROM word_table order by word")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word LIKE :search order by word")
    fun getWordsForSearch(search: String): LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE game LIKE :game order by word")
    fun getAllWordsForGame(game: String): LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word LIKE :search AND game LIKE :game order by word")
    fun getAllWordsLikeSearchForGame(search: String, game: String): LiveData<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(words: List<Word>)

}