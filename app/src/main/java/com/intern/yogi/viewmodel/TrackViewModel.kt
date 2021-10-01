package com.intern.yogi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intern.yogi.BooksResponse
import com.intern.yogi.repo.TrackRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrackViewModel(val trackRepo: TrackRepo): ViewModel() {

    val booksStateFlow = MutableStateFlow<BooksResponse?>(null)

    init {
        viewModelScope.launch {
            trackRepo.getBookDetails().collect {
                booksStateFlow.value = it
            }
        }
    }

    fun getBooksInfo() = trackRepo.getBookDetails()
}