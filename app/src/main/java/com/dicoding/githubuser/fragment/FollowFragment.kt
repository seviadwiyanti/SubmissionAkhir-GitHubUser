package com.dicoding.githubuser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.databinding.FragmentFollowBinding
import com.dicoding.githubuser.ui.DetailUserViewModel
import com.dicoding.githubuser.ui.UserAdapter

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding
    private lateinit var viewModel: DetailUserViewModel
    private lateinit var userAdapter: UserAdapter
    private var position: Int = 0
    private var username: String = ""

    companion object{
        const val ARG_POSTION = "position"
        const val ARG_USERNAME = "username"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_POSTION)
            username= it.getString(ARG_USERNAME) ?: ""
        }
        viewModel = ViewModelProvider(this).get(DetailUserViewModel::class.java)
        userAdapter = UserAdapter()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        if (position == 1){
            viewModel.followers.observe(viewLifecycleOwner){followers ->
                userAdapter.submitList(followers)
            }
        }else{
            viewModel.following.observe(viewLifecycleOwner){following ->
                userAdapter.submitList(following)
            }
        }
        binding.rvfollows.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvfollows.adapter = userAdapter

        if (position == 1){
            viewModel.getFollowers(username)
        }else{
            viewModel.getFollowing(username)
        }
    }

}


