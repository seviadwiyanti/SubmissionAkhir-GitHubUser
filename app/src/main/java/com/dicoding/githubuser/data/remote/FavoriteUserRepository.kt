package com.dicoding.githubuser.data.remote

import androidx.lifecycle.LiveData
import com.dicoding.githubuser.data.local.Users
import com.dicoding.githubuser.data.local.FavoriteUserDao


class FavoriteUserRepository(private val favoriteUserDao: FavoriteUserDao) {

    fun getAllFavoriteUsers(): LiveData<List<Users>> {
        return favoriteUserDao.getAllFavoriteUsers()
    }

    fun insert(users: Users) {
        favoriteUserDao.insert(users)
    }

    fun delete(users: Users) {
        favoriteUserDao.delete(users)
    }
}


