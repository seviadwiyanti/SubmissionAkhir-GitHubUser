package com.dicoding.githubuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Users(
    @PrimaryKey(autoGenerate = false)
    var username: String = "",
    var avatarUrl: String? = null,
)

