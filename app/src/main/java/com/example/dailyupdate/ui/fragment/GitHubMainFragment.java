package com.example.dailyupdate.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.GitHubRepo;
import com.example.dailyupdate.ui.adapter.GitHubRepoAdapter;
import com.example.dailyupdate.viewmodels.GitHubViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GitHubMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.main_emptyview) TextView emptyView;
    @BindView(R.id.main_spinner) ProgressBar spinner;

    private String searchKeyword;
    private String sortBy;
    private String searchOrder;
    private SharedPreferences sharedPref;
    private GitHubMainFragmentListener listener;
    private GitHubViewModel gitHubViewModel;
    private GitHubRepoAdapter gitHubRepoAdapter;

    public interface GitHubMainFragmentListener {
        void currentGitHubRepoUrl(String gitHubRepoUrl);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GitHubMainFragmentListener) {
            listener = (GitHubMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "GitHubMainFragmentListener");
        }
    }

    public GitHubMainFragment() {
    }

    public static GitHubMainFragment newInstance() {
        return new GitHubMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spinner.setVisibility(View.VISIBLE);

        gitHubViewModel = ViewModelProviders.of(this).get(GitHubViewModel.class);
        subscribeGitHubObserver();

        getSharedPreferences();

        // Fetch data from API only if the search keyword is not empty
        if (searchKeyword.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        } else {
            gitHubViewModel.searchGitHubRepoListWithOrder(searchKeyword, sortBy, searchOrder);
        }
        return rootView;
    }

    /**
     * Get the search criterias stocked in the SharedPreferences
     **/
    private void getSharedPreferences() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        searchKeyword = sharedPref.getString(getString(R.string.pref_github_edittext_key), "");
        sortBy = sharedPref.getString(getString(R.string.pref_github_sort_key),
                getString(R.string.pref_github_sort_default));
        searchOrder = sharedPref.getString(getString(R.string.pref_github_order_key),
                getString(R.string.pref_github_order_default));
    }

    private void subscribeGitHubObserver() {
        gitHubViewModel.getGitHubRepoList().observe(this, new Observer<List<GitHubRepo>>() {
            @Override
            public void onChanged(List<GitHubRepo> gitHubRepoList) {
                if (gitHubRepoList != null) {
                    spinner.setVisibility(View.GONE);
                    gitHubRepoAdapter = new GitHubRepoAdapter(getContext(), gitHubRepoList, 2);
                    recyclerView.setAdapter(gitHubRepoAdapter);

                    gitHubRepoAdapter.setOnItemClickListener((position, v) -> {
                        GitHubRepo gitHubRepo = gitHubRepoList.get(position);
                        String gitHubRepoUrl = gitHubRepo.getHtmlUrl();
                        listener.currentGitHubRepoUrl(gitHubRepoUrl);
                    });
                }
            }
        });
    }
}
