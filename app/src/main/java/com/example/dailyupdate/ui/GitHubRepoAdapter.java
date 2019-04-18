package com.example.dailyupdate.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.GitHubRepo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GitHubRepoAdapter extends RecyclerView.Adapter<GitHubRepoAdapter.GitHubRepoAdapterViewHolder> {

    private Context context;
    private final List<GitHubRepo> gitHubRepoList;
    private static ClickListener clickListener;
    private String repoDescription;
    private String repoLanguage;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

//    public void setOnItemClickListener(ClickListener clickListener) {
//        GitHubRepoAdapter.clickListener = clickListener;
//    }

    public GitHubRepoAdapter(Context context, List<GitHubRepo> gitHubRepoList) {
        this.context = context;
        this.gitHubRepoList = gitHubRepoList;
    }


    @Override
    public GitHubRepoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.home_githubrepo_item;
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

