package com.chakaseptember.boardgameglossary.network


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface GlossaryService {
    @GET("data.json")//@GET("4qQHWkHK")
    fun getGlossary(): Deferred<GlossaryContainer>
}

object WordNetwork {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://boardgameglossary.chakaseptember.com/")//"https://pastebin.com/raw/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val glossary = retrofit.create(GlossaryService::class.java)
}