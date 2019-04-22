package com.example.dailyupdate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.GitHubRepo;
import com.example.dailyupdate.data.GitHubResponse;
import com.example.dailyupdate.networking.GitHubService;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.ui.adapter.GitHubRepoAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dailyupdate.ui.fragment.GitHubDialogFragment.KEY_GITHUB_KEYWORD;
import static com.example.dailyupdate.ui.fragment.GitHubDialogFragment.KEY_GITHUB_ORDER;
import static com.example.dailyupdate.ui.fragment.GitHubDialogFragment.KEY_GITHUB_SORT_BY;

public class GitHubMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;

    private String searchKeyword = "android";
    private String sortBy;
    private String searchOrder;

    public GitHubMainFragment() {
    }

    public static GitHubMainFragment newInstance(Bundle searchArguments) {
        GitHubMainFragment gitHubMainFragment = new GitHubMainFragment();
        Bundle args = new Bundle();
        args.putBundle("KEY_GITHUB_SEARCH_ARGUMENTS", searchArguments);
        gitHubMainFragment.setArguments(args);
        return gitHubMainFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments().getParcelable("KEY_GITHUB_SEARCH_ARGUMENTS");
        if (!bundle.isEmpty()) {
            searchKeyword = bundle.getString(KEY_GITHUB_KEYWORD);
            sortBy = bundle.getString(KEY_GITHUB_SORT_BY);
            searchOrder = bundle.getString(KEY_GITHUB_ORDER);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        retrieveMeetupGroups();
        return rootView;
    }

    private void retrieveMeetupGroups() {
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
