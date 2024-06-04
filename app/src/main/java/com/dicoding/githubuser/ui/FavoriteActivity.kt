package com.dicoding.githubuser.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.data.local.FavoriteDatabase
import com.dicoding.githubuser.data.remote.FavoriteUserRepository
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.databinding.ActivityFavoriteBinding


class FavoriteActivity : AppCompatActivity() {
    private lateinit var userAdapter: UserAdapter
    private lateinit var repository: FavoriteUserRepository
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var binding: ActivityFavoriteBinding

    companion object {
        private const val DELETE_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDatabase = FavoriteDatabase.getInstance(applicationContext)
        val favoriteUserDao = userDatabase.favoriteUserDao()

        repository = FavoriteUserRepository(favoriteUserDao)

        val viewModelFactory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)

        userAdapter = UserAdapter()

        val recyclerView: RecyclerView = binding.RvFavorite
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        viewModel.getAllFavoriteUsers().observe(this) { favoriteUsers ->
            val items = favoriteUsers.map {
                ItemsItem(login = it.username, avatarUrl = it.avatarUrl)
            }
            userAdapter.submitList(items)
        }

        userAdapter.setRecyclerViewClickListener(object : UserAdapter.RecyclerViewClickListener {
            override fun onItemClick(view: View, item: ItemsItem) {
                onFavoriteItemClick(item)
            }
        })
    }

    private fun onFavoriteItemClick(item: ItemsItem) {
        binding.progressBar.visibility = View.VISIBLE
        val intent = Intent(this@FavoriteActivity, DetailUserActivity::class.java).apply {
            putExtra("EXTRA_USERNAME", item.login)
            putExtra("EXTRA_AVATARURL", item.avatarUrl)
        }
        startActivityForResult(intent, DELETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val usernameDeleted = data?.getStringExtra("USERNAME_DELETED")
            usernameDeleted?.let { username ->
                val newList = userAdapter.currentList.filter { it.login != username }
                userAdapter.submitList(newList)
            }
        }
        binding.progressBar.visibility = View.GONE
    }


}