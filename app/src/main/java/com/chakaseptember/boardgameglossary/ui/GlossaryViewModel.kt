package com.chakaseptember.boardgameglossary.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.chakaseptember.boardgameglossary.database.Word
import com.chakaseptember.boardgameglossary.database.getWordDatabaseInstance
import com.chakaseptember.boardgameglossary.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

class GlossaryViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(getWordDatabaseInstance(application))


    var words = wordRepository.words
    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = SupervisorJob()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        refreshDataFromRepository()
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                wordRepository.refreshWords()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {

                Log.e("Network error", networkError.toString(), networkError)
                // Show a Toast error message and hide the progress bar.
                if (words.value!!.isEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    fun refreshWords() {
        viewModelScope.launch {
            try {
                wordRepository.refreshWords()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                Log.e("Network error", networkError.toString(), networkError)
                // Show a Toast error message and hide the progress bar.
                if (words.value!!.isEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    fun searchWords(search: String): LiveData<List<Word>> {
        words = wordRepository.getWordsForSearch(search)
        return words
    }


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }


    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GlossaryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GlossaryViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }


}
