package com.example.dailyupdate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyupdate.R
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.utilities.NetworkUtilities
import kotlinx.android.synthetic.main.github_main_repo_item.view.*
import kotlinx.android.synthetic.main.home_githubrepo_item.view.*

class GitHubRepoAdapter : RecyclerView.Adapter<GitHubRepoAdapter.GitHubRepoViewHolder>() {

    private val gitHubRepoList = mutableListOf<GitHubRepo>()
    private var currentLayoutOption: Int = 1

    fun setGitHubItemList(list: List<GitHubRepo>?, layoutOption: Int) {
        currentLayoutOption = layoutOption
        gitHubRepoList.apply {
            // If the new list is different from current one, update the current one
            if (list != null && gitHubRepoList != list) {
                clear()
                addAll(list)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubRepoViewHolder {
        val context = parent.context
        val view: View
        // Use a different layout for Home and GitHub Search
        when (currentLayoutOption) {
            1 -> view = LayoutInflater.from(context).inflate(R.layout.home_githubrepo_item, parent, false)
            2 -> view = LayoutInflater.from(context).inflate(R.layout.github_main_repo_item, parent, false)
            else -> throw IllegalArgumentException("Unknown layout")
        }
        val holder = GitHubRepoViewHolder(view)

        //Open the selected GitHub repository details using WebView(Custom Tabs)
        view.setOnClickListener {
            val position = holder.adapterPosition
            NetworkUtilities.openCustomTabs(context, gitHubRepoList[position].htmlUrl);
        }
        return holder
    }


    override fun onBindViewHolder(viewHolder: GitHubRepoViewHolder, i: Int) {
        when (currentLayoutOption) {
            1 -> viewHolder.setHomeData(gitHubRepoList[i])
            2 -> viewHolder.setSearchData(gitHubRepoList[i])
            else -> throw IllegalArgumentException("Unknown view holder")
        }
    }

    override fun getItemCount(): Int {
        return gitHubRepoList.size
    }

    class GitHubRepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun setHomeData(data: GitHubRepo?) {
            itemView.apply {
                home_github_textview_title.text = data?.name

                if (data?.description.isNullOrEmpty()) {
                    home_github_textview_description.text = context.getString(R.string.repository_no_description)
                } else {
                    home_github_textview_description.text = data?.description
                }

                if (data?.description.isNullOrEmpty()) {
                    home_github_textview_language.text = context.getString(R.string.repository_no_language)
                } else {
                    home_github_textview_language.text = data?.language
                }

                home_github_textview_stars.text = context.getString(R.string
                        .repository_stars_label, data?.starCount.toString())
            }
        }

        fun setSearchData(data: GitHubRepo?) {
            itemView.apply {
                main_github_textview_title.text = data?.name

                if (data?.description.isNullOrEmpty()) {
                    main_github_textview_description.text = context.getString(R.string.repository_no_description)
                } else {
                    main_github_textview_description.text = data?.description
                }

                if (data?.description.isNullOrEmpty()) {
                    main_github_textview_language.text = context.getString(R.string.repository_no_language)
                } else {
                    main_github_textview_language.text = data?.language
                }

                main_github_textview_stars.text = context.getString(R.string
                        .repository_stars_label, data?.starCount.toString())
            }
        }
    }
}

