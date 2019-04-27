package com.example.dailyupdate.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.GitHubRepo;
import com.example.dailyupdate.data.model.GitHubResponse;
import com.example.dailyupdate.networking.GitHubService;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.ui.adapter.GitHubRepoAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;

    private String searchKeyword;
    private String sortBy;
    private String searchOrder;
    private SharedPreferences sharedPref;

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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        searchKeyword = sharedPref.getString(getString(R.string.pref_github_edittext_key), "");
        sortBy = sharedPref.getString(getString(R.string.pref_github_sort_key),
                getString(R.string.pref_github_sort_default));
        searchOrder = sharedPref.getString(getString(R.string.pref_github_order_key),
                getString(R.string.pref_github_order_default));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (searchKeyword.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        } else {
            retrieveGitHubRepository();
        }
        return rootView;
    }

    private void retrieveGitHubRepository() {
        GitHubService gitHubservice =
                RetrofitInstance.getGitHubRetrofitInstance().create(GitHubService.class);
        Call<GitHubResponse> repoListCall =
                gitHubservice.getGitHubRepoListWithOrder(searchKeyword, sortBy, searchOrder);
        repoListCall.enqueue(new Callback<GitHubResponse>() {
            @Override
            public void onResponse(Call<GitHubResponse> call, Response<GitHubResponse> response) {
                GitHubResponse gitHubResponse = response.body();
                List<GitHubRepo> gitHubRepoList = gitHubResponse.getGitHubRepo();

                GitHubRepoAdapter gitHubRepoAdapter = new GitHubRepoAdapter(getContext(),
                        gitHubRepoList, 2);
                recyclerView.setAdapter(gitHubRepoAdapter);
            }

            @Override
            public void onFailure(Call<GitHubResponse> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }
}
