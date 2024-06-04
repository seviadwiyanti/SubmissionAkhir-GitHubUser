package com.dicoding.githubuser.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: Users)

    @Query("SELECT * FROM users")
    fun getAllFavoriteUsers(): LiveData<List<Users>>

    @Delete
    fun delete(users: Users)

    @Query("SELECT * FROM Users WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<Users>
}

