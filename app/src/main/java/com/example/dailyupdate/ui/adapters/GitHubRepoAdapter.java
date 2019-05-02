package com.example.dailyupdate.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.GitHubRepo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GitHubRepoAdapter extends RecyclerView.Adapter<GitHubRepoAdapter.GitHubRepoAdapterViewHolder> {

    @BindView(R.id.textview_title) TextView repoTitleTextView;
    @BindView(R.id.textview_description) TextView repoDescriptionTextView;
    @BindView(R.id.textview_language) TextView repoLanguageTextView;
    @BindView(R.id.textview_stars) TextView repoStarsTextView;

    private Context context;
    private final List<GitHubRepo> gitHubRepoList;
    private static ClickListener clickListener;
    private String repoDescription;
    private String repoLanguage;
    private int layoutOption;
    private int layoutIdForListItem;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        GitHubRepoAdapter.clickListener = clickListener;
    }

    public GitHubRepoAdapter(Context context, List<GitHubRepo> gitHubRepoList, int layoutOption) {
        this.context = context;
        this.gitHubRepoList = gitHubRepoList;
        this.layoutOption = layoutOption;
    }

    @Override
    public GitHubRepoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        // Use a different layout for Home and GitHub Search
        if(layoutOption == 1){
            layoutIdForListItem = R.layout.home_githubrepo_item;
        } else if (layoutOption == 2){
            layoutIdForListItem = R.layout.github_main_repo_item;
        }
        View view = LayoutInflater.from(context).inflate(layoutIdForListItem, parent, false);
        ButterKnife.bind(this, view);
        return new GitHubRepoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GitHubRepoAdapterViewHolder viewHolder, int i) {
        GitHubRepo gitHubRepoItem = gitHubRepoList.get(i);
        if (gitHubRepoItem.getDescription() == null) {
            repoDescription = context.getString(R.string.repository_no_description);
        } else {
            repoDescription = gitHubRepoItem.getDescription();
        }
        if (gitHubRepoItem.getDescription() == null) {
            repoLanguage = context.getString(R.string.repository_no_language);
        } else {
            repoLanguage = gitHubRepoItem.getLanguage();
        }
        String repoStarsString = gitHubRepoItem.getStarCount() + context.getString(R.string.repository_stars_label);

        repoTitleTextView.setText(gitHubRepoItem.getName());
        repoDescriptionTextView.setText(repoDescription);
        repoLanguageTextView.setText(repoLanguage);
        repoStarsTextView.setText(repoStarsString);
    }

    @Override
    public int getItemCount() {
        if (null == gitHubRepoList) return 0;
        return gitHubRepoList.size();
    }

    public class GitHubRepoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GitHubRepoAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}

