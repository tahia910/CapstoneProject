package com.example.dailyupdate.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GitHubResponse {

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("incomplete_result")
    private boolean incompleteResult;
    @SerializedName("items")
    private List<GitHubRepo> gitHubRepo;

    public GitHubResponse(int totalCount, boolean incompleteResult, List<GitHubRepo> gitHubRepo) {
        this.totalCount = totalCount;
        this.incompleteResult = incompleteResult;
        this.gitHubRepo = gitHubRepo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public Boolean getIncompleteResult() {
        return incompleteResult;
    }

    public List<GitHubRepo> getGitHubRepo() {
        return gitHubRepo;
    }
}
