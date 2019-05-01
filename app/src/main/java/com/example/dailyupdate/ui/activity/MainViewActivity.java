package com.example.dailyupdate.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragment.GitHubDialogFragment;
import com.example.dailyupdate.ui.fragment.GitHubMainFragment;
import com.example.dailyupdate.ui.fragment.MeetupDetailsFragment;
import com.example.dailyupdate.ui.fragment.MeetupDialogFragment;
import com.example.dailyupdate.ui.fragment.MeetupMainFragment;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.NetworkUtilities;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener, GitHubDialogFragment.GitHubDialogListener, MeetupMainFragment.MeetupMainFragmentListener, GitHubMainFragment.GitHubMainFragmentListener {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private FragmentManager fragmentManager;
    private String mainViewOption;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPref;
    private String meetupSearchKeyword;
    private String gitHubSearchKeyword;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        ButterKnife.bind(this);
        setActionBar();

        fragmentManager = getSupportFragmentManager();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mainViewOption = bundle.getString(Constants.MAIN_KEY);
        }
        openDialogOrFragment();
    }

    /**
     * Decide which fragment or dialog to display from this activity
     **/
    private void openDialogOrFragment() {
        if (mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.meetup_search_title));
            meetupSearchKeyword =
                    sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
            if (!meetupSearchKeyword.isEmpty()) {
                getMeetupFragment();
            } else {
                getSearchDialog();
            }
        } else if (mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.github_search_title));
            gitHubSearchKeyword =
                    sharedPref.getString(getString(R.string.pref_github_edittext_key), "");
            if (!gitHubSearchKeyword.isEmpty()) {
                getGitHubFragment();
            } else {
                getSearchDialog();
            }
        }
    }

    /**
     * Set up the toolbar and drawer
     **/
    private void setActionBar() {
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    /**
     * Get the search dialog for either Meetup events or GitHub repositories
     **/
    private void getSearchDialog() {
        if (mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
            DialogFragment meetupDialogFragment = new MeetupDialogFragment();
            meetupDialogFragment.show(fragmentManager, "meetup_search");
        } else if (mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
            DialogFragment gitHubDialogFragment = new GitHubDialogFragment();
            gitHubDialogFragment.show(fragmentManager, "github_search");
        }
    }

    private void getMeetupFragment() {
        MeetupMainFragment meetupFragment = MeetupMainFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, meetupFragment).commit();
    }

    private void getGitHubFragment() {
        GitHubMainFragment gitHubFragment = GitHubMainFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, gitHubFragment).commit();
    }

    /**
     * Callback from MeetupDialogFragment (Meetup events search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button
     **/
    @Override
    public void onMeetupDialogPositiveClick(DialogFragment dialog) {
        meetupSearchKeyword = sharedPref.getString(getString(R.string.pref_meetup_edittext_key),
                "");
        if (!meetupSearchKeyword.isEmpty()) {
            getMeetupFragment();
        } else {
            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMeetupDialogNegativeClick(DialogFragment dialog) {
        // TODO: handle meetup dialog cancel option(2)
    }

    /**
     * Callback from GitHubDialogFragment (GitHub search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button
     **/
    @Override
    public void onGitHubDialogPositiveClick(DialogFragment dialog) {
        gitHubSearchKeyword = sharedPref.getString(getString(R.string.pref_github_edittext_key),
                "");
        if (!gitHubSearchKeyword.isEmpty()) {
            getGitHubFragment();
        } else {
            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGitHubDialogNegativeClick(DialogFragment dialog) {
        // TODO: handle github dialog cancel option(2)
    }

    /**
     * Callback from MeetupMainFragment (fragment displaying the result of a search) to open the
     * details of an event
     **/
    @Override
    public void currentEventInfo(String groupUrl, String eventId) {
        MeetupDetailsFragment meetupDetailsFragment = MeetupDetailsFragment.newInstance(groupUrl,
                eventId);
        meetupDetailsFragment.setHasOptionsMenu(true);
        meetupDetailsFragment.setMenuVisibility(true);
        meetupDetailsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        meetupDetailsFragment.show(getSupportFragmentManager(), "meetup_details");
    }

    /**
     * Callback from GitHubMainFragment (fragment displaying the result of a search) to open the
     * GitHub repository details using WebView(Custom Tabs)
     **/
    @SuppressLint("NewApi")
    @Override
    public void currentGitHubRepoUrl(String gitHubRepoUrl) {
        NetworkUtilities.openCustomTabs(getApplicationContext(), gitHubRepoUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                getSearchDialog();
                return true;
            case R.id.action_pref:
                Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
                startActivity(preferenceIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.content_description_open_drawer,
                R.string.content_description_close_drawer);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            selectDrawerItem(menuItem);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_bookmarks:
                startActivity(new Intent(this, BookmarksActivity.class));
                break;
            case R.id.nav_github:
                if (!mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
                    Intent gitHubIntent = new Intent(this, MainViewActivity.class);
                    gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY);
                    startActivity(gitHubIntent);
                }
                break;
            case R.id.nav_meetup:
                if (!mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
                    Intent meetupIntent = new Intent(this, MainViewActivity.class);
                    meetupIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY);
                    startActivity(meetupIntent);
                }
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }
}
