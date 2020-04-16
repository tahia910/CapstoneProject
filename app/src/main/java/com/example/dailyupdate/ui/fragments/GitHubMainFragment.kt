package com.example.dailyupdate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyupdate.R
import com.example.dailyupdate.ui.adapters.GitHubRepoAdapter
import com.example.dailyupdate.utilities.Constants
import com.example.dailyupdate.utilities.NetworkUtilities
import com.example.dailyupdate.viewmodels.GitHubViewModel
import kotlinx.android.synthetic.main.main_layout.*

class GitHubMainFragment : Fragment() {

    private var searchKeyword: String = ""
    private var sortBy: String = ""
    private var searchOrder: String = ""
    private lateinit var gitHubViewModel: GitHubViewModel
    private var recyclerViewLastPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.main_layout, container, false)

        gitHubViewModel = ViewModelProvider(this).get(GitHubViewModel::class.java)
        getSharedPreferences()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        main_recycler_view.layoutManager = LinearLayoutManager(context)
        main_spinner.visibility = View.VISIBLE

        subscribeGitHubObserver()
        // Fetch data from API only if the search keyword is not empty
        if (searchKeyword.isEmpty()) {
            Toast.makeText(context, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show()
        } else {
            // Check if the network is available first, display empty view if there is no connection
            val isConnected = NetworkUtilities.checkNetworkAvailability(context!!)
            if (!isConnected) {
                main_recycler_view.visibility = View.INVISIBLE
                main_spinner.visibility = View.GONE
                main_emptyview.visibility = View.VISIBLE
                main_emptyview.text = getString(R.string.no_internet_connection)
            } else {
                gitHubViewModel.searchGitHubRepoList(searchKeyword, sortBy, searchOrder)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            recyclerViewLastPosition = savedInstanceState.getInt(Constants.KEY_GITHUB_RECYCLERVIEW_POSITION)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewLastPosition = (main_recycler_view?.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        outState.putInt(Constants.KEY_GITHUB_RECYCLERVIEW_POSITION, recyclerViewLastPosition)
    }

    /**
     * Get the search criterias stocked in the SharedPreferences
     */
    private fun getSharedPreferences() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context!!)
        searchKeyword = sharedPref.getString(getString(R.string.pref_github_edittext_key), "")!!
        sortBy = sharedPref.getString(getString(R.string.pref_github_sort_key),
                getString(R.string.pref_github_sort_default))!!
        searchOrder = sharedPref.getString(getString(R.string.pref_github_order_key),
                getString(R.string.pref_github_order_default))!!
    }

    private fun subscribeGitHubObserver() {
        gitHubViewModel.gitHubRepoList.observe(viewLifecycleOwner, Observer { gitHubRepoList ->
            main_spinner.visibility = View.GONE
            if (gitHubRepoList != null) {
                val adapter = GitHubRepoAdapter()
                adapter.setGitHubItemList(gitHubRepoList, 2)
                main_recycler_view.adapter = adapter

                // If there was a screen rotation, restore the previous position
                if (recyclerViewLastPosition != 0) {
                    (main_recycler_view.layoutManager as LinearLayoutManager).scrollToPosition(recyclerViewLastPosition)
                }

                // Set the swipe action to refresh the search
                main_swipe_refresh_layout.setOnRefreshListener {
                    gitHubViewModel.searchGitHubRepoList(searchKeyword, sortBy, searchOrder)
                    main_swipe_refresh_layout.isRefreshing = false
                }
            } else {
                main_recycler_view.visibility = View.INVISIBLE
                main_emptyview.visibility = View.VISIBLE
                main_emptyview.text = getString(R.string.github_error_message)
            }
        })
    }

    companion object {

        fun newInstance(): GitHubMainFragment {
            return GitHubMainFragment()
        }
    }
}
