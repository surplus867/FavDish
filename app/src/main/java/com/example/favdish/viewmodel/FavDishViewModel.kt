package com.example.favdish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.entities.FavDish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(dish)
    }
