package com.dicoding.githubuser.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.response.GithubResponse
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.data.retrofit.ApiConfig
import com.dicoding.githubuser.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), UserAdapter.RecyclerViewClickListener {

    private lateinit var binding: ActivityMainBinding
    private val adapter = UserAdapter()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    val value = searchView.text.toString()
                    findItem(value)
                    false
                }
        }

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.items.observe(this) { items ->
            setUserData(listOf(items))
        }
        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager

        mainViewModel.itemsListUser.observe(this){itemsItem ->
            setUserData(itemsItem)
        }

        MainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        getData("username")
    }

    private fun getData(query: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getItemsItem(query)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(call: Call<GithubResponse>, response: Response<GithubResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setUserData(responseBody.items)
                    }
                } else {
                    Log.e(TAG, "onFailure:${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure:${t.message}")
            }
        })
    }

    private fun findItem(string: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().getItemsItem(string)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(call: Call<GithubResponse>, response: Response<GithubResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setUserData(responseBody.items)
                    }
                } else {
                    Log.e(TAG, "onFailure:${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure:${t.message}")
            }
        })
    }

    private fun setUserData(items: List<ItemsItem?>?) {
        adapter.submitList(items)
        binding.rvUser.adapter = adapter
        adapter.listener = this
    }

    private fun showLoading(isLoading:Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View, item: ItemsItem) {
        val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
        intent.putExtra("EXTRA_ID", item.id)
        intent.putExtra("EXTRA_USERNAME", item.login)
        intent.putExtra("EXTRA_AVATARURL", item.avatarUrl)
        intent.putExtra("EXTRA_FOLLOWERS", item.followersUrl)
        intent.putExtra("EXTRA_FOLLOWING", item.followingUrl)
        Log.d(TAG, "onItemClick "+item.login)
        startActivity(intent).apply {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favorite -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_setting -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onFavoriteIconClicked(item: MenuItem) {
        Log.d(TAG, "favorit!")
        startActivity(Intent(this, FavoriteActivity::class.java))
    }

    fun onSettingIconClicked(item: MenuItem) {
        Log.d(TAG, "setting !")
        startActivity(Intent(this, SettingActivity::class.java))
    }

}