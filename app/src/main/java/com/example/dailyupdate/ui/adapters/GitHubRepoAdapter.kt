package com.example.dailyupdate.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyupdate.R
import com.example.dailyupdate.data.models.GitHubRepo
import kotlinx.android.synthetic.main.github_main_repo_item.view.*
import kotlinx.android.synthetic.main.home_githubrepo_item.view.*

class GitHubRepoAdapter(val context: Context?, private val gitHubRepoList: List<GitHubRepo>?,
                        private val layoutOption: Int) : RecyclerView.Adapter<GitHubRepoAdapter.GitHubRepoViewHolder>() {

    companion object {
        private var clickListener: ClickListener? = null
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        GitHubRepoAdapter.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubRepoAdapter.GitHubRepoViewHolder {
        var view = View(context)
        // Use a different layout for Home and GitHub Search
        when (layoutOption) {
            1 -> view = LayoutInflater.from(context).inflate(R.layout.home_githubrepo_item, parent, false)
            2 -> view = LayoutInflater.from(context).inflate(R.layout.github_main_repo_item, parent, false)
            else -> throw IllegalArgumentException("Unknown layout")
        }
        val holder = GitHubRepoViewHolder(view)

        // TODO: set clicklistener

        return holder
    }


    override fun onBindViewHolder(viewHolder: GitHubRepoViewHolder, i: Int) {
        when (layoutOption) {
            1 -> viewHolder.setHomeData(gitHubRepoList?.get(i))
            2 -> viewHolder.setSearchData(gitHubRepoList?.get(i))
            else -> throw IllegalArgumentException("Unknown view holder")
        }
    }

    override fun getItemCount(): Int {
        return gitHubRepoList?.size ?: 0
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

