package com.xdroid.app.changewallpaper.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartboost.sdk.impl.id
import com.xdroid.app.changewallpaper.data.repository.FavoriteRepository
import com.xdroid.app.changewallpaper.data.room.Favorites
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: FavoriteRepository) : ViewModel() {
    val notes = repo.notes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addNote(content: String) {
        viewModelScope.launch {
            repo.insert(Favorites(url = content))
        }
    }

    private val _singleItem = MutableStateFlow<Favorites?>(null)
    val singleItem: StateFlow<Favorites?> = _singleItem

    fun loadSingleItem(content: String) {
        repo.getSingleItem(content)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
            .onEach { _singleItem.value = it }
            .launchIn(viewModelScope)
    }

    fun deleteNote(note: Favorites) {
        viewModelScope.launch {
            repo.delete(note)
        }
    }
}