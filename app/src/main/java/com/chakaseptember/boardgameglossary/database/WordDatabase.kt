package com.chakaseptember.boardgameglossary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Word::class), version = 1)
abstract class WordDatabase : RoomDatabase() {

    abstract val wordDoa: WordDao

}

private lateinit var INSTANCE: WordDatabase

fun getWordDatabaseInstance(context: Context): WordDatabase {
    synchronized(WordDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                WordDatabase::class.java,
                "WordDatabase"
            ).build()
        }
    }
    return INSTANCE
}
