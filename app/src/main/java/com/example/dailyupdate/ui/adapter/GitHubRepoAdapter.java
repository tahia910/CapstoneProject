package com.example.dailyupdate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.GitHubRepo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GitHubRepoAdapter extends RecyclerView.Adapter<GitHubRepoAdapter.GitHubRepoAdapterViewHolder> {

    private Context context;
    private final List<GitHubRepo> gitHubRepoList;
    private static ClickListener clickListener;
    private String repoDescription;
    private String repoLanguage;
    private int layoutOption;
    int layoutIdForListItem;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

//    public void setOnItemClickListener(ClickListener clickListener) {
//        GitHubRepoAdapter.clickListener = clickListener;
//    }

    public GitHubRepoAdapter(Context context, List<GitHubRepo> gitHubRepoList, int layoutOption) {
        this.context = context;
        this.gitHubRepoList = gitHubRepoList;
        this.layoutOption = layoutOption;
    }


    @Override
    public GitHubRepoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        if(layoutOption == 1){
            layoutIdForListItem = R.layout.home_githubrepo_item;
        } else if (layoutOption == 2){
            layoutIdForListItem = R.layout.github_main_repo_item;
        }
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        GitHubRepoAdapterViewHolder viewHolder = new GitHubRepoAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GitHubRepoAdapterViewHolder viewHolder, int i) {
        GitHubRepo gitHubRepoItem = gitHubRepoList.get(i);
        String repoTitle = gitHubRepoItem.getName();
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
        int repoStars = gitHubRepoItem.getStarCount();
        String repoStarsString =
                String.valueOf(repoStars) + context.getString(R.string.repository_stars_label);

        viewHolder.repoTitleTextView.setText(repoTitle);
        viewHolder.repoDescriptionTextView.setText(repoDescription);
        viewHolder.repoLanguage.setText(repoLanguage);
        viewHolder.repoStars.setText(repoStarsString);
    }

    @Override
    public int getItemCount() {
        if (null == gitHubRepoList) return 0;
        return gitHubRepoList.size();
    }

    public class GitHubRepoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView repoTitleTextView;
        private final TextView repoDescriptionTextView;
        private final TextView repoLanguage;
        private final TextView repoStars;

        private GitHubRepoAdapterViewHolder(View view) {
            super(view);
            repoTitleTextView = (TextView) view.findViewById(R.id.textview_title);
            repoDescriptionTextView = (TextView) view.findViewById(R.id.textview_description);
            repoLanguage = (TextView) view.findViewById(R.id.textview_language);
            repoStars = (TextView) view.findViewById(R.id.textview_stars);
//            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}

