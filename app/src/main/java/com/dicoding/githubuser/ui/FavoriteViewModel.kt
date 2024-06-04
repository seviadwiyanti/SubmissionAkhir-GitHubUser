package com.dicoding.githubuser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.local.Users
import com.dicoding.githubuser.data.remote.FavoriteUserRepository


class FavoriteViewModel(private val repository: FavoriteUserRepository) : ViewModel() {

    fun getAllFavoriteUsers(): LiveData<List<Users>> {
        return repository.getAllFavoriteUsers()
    }
}