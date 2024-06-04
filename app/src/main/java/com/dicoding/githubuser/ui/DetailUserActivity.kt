package com.dicoding.githubuser.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.local.FavoriteDatabase
import com.dicoding.githubuser.data.local.FavoriteUserDao
import com.dicoding.githubuser.data.local.Users
import com.dicoding.githubuser.data.remote.FavoriteUserRepository
import com.dicoding.githubuser.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var detailUserViewModel: DetailUserViewModel
    private lateinit var adapter: SectionPagerAdapter
    private lateinit var favoriteUserDao: FavoriteUserDao
    private lateinit var userRepository: FavoriteUserRepository


    private var isFavorite: Boolean = false
    private lateinit var favoriteDatabase: FavoriteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteDatabase = Room.databaseBuilder(applicationContext, FavoriteDatabase::class.java, "user_database").build()
        userRepository = FavoriteUserRepository(favoriteDatabase.favoriteUserDao())
        favoriteUserDao = favoriteDatabase.favoriteUserDao()

        val username = intent.getStringExtra("EXTRA_USERNAME")

        detailUserViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailUserViewModel::class.java)
        username?.let { detailUserViewModel.getUserDetail(it) }

        adapter = SectionPagerAdapter(this@DetailUserActivity)
        adapter.username = username ?: ""

        binding.viewPager.adapter = adapter

        binding.fabFav.setOnClickListener {
            toggleFavoriteStatus()
        }

        observeFavoriteStatus(username ?: "")

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Followers"
                1 -> "Following"
                else -> ""
            }
        }.attach()

        detailUserViewModel.userDetail.observe(this) { userDetail ->
            binding.apply {
                tvUsername.text = userDetail.login
                tvName.text = userDetail.name
                tvFollowers.text = "${userDetail.followers} Followers"
                tvFollowing.text = "${userDetail.following} Following"
                Glide.with(this@DetailUserActivity)
                    .load(userDetail.avatarUrl)
                    .into(imgProfile)
            }
        }
        detailUserViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeFavoriteStatus(username: String) {
        if (::favoriteUserDao.isInitialized) {
            favoriteUserDao.getAllFavoriteUsers().observe(this) { favoriteUsers ->
                val favoriteUser = favoriteUsers.find { it.username == username }
                isFavorite = favoriteUser != null
                updateFavoriteButtonIcon()
            }
        }
    }

    private fun updateFavoriteButtonIcon() {
        val iconRes = if (isFavorite) R.drawable.favorite_out else R.drawable.favorite_in
        binding.fabFav.setImageResource(iconRes)
    }

    private fun toggleFavoriteStatus() {
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: return
        val avatarUrl = intent.getStringExtra("EXTRA_AVATARURL")

        if (isFavorite) {
            delete(username)
        } else {
            insert(username, avatarUrl)
        }
    }

    private fun insert(username: String, avatarUrl: String?) {
        val favoriteUser = Users(username, avatarUrl)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.insert(favoriteUser)
            }
            isFavorite = true
            updateFavoriteButtonIcon()
        }
    }

    private fun delete(username: String) {
        val favoriteUser = Users(username)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.delete(favoriteUser)
            }
            isFavorite = false
            updateFavoriteButtonIcon()

            val resultIntent = Intent()
            resultIntent.putExtra("USERNAME_DELETED", username)
            setResult(Activity.RESULT_OK, resultIntent)
        }
    }

}