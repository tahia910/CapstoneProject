package com.example.dailyupdate.ui;

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
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dailyupdate.ui.MainActivity.GITHUB_MAIN_KEY;
import static com.example.dailyupdate.ui.MainActivity.MAIN_KEY;
import static com.example.dailyupdate.ui.MainActivity.MEETUP_MAIN_KEY;

public class MainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener, GitHubDialogFragment.GitHubDialogListener, MeetupMainFragment.MeetupMainFragmentListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    public static final String KEY_GROUP_URL = "keyGroupUrl";
    public static final String KEY_EVENT_ID = "keyEventId";
    MeetupMainFragment meetupFragment;
    GitHubMainFragment gitHubFragment;
    private FragmentManager fragmentManager;
    private String mainViewOption;
    ActionBarDrawerToggle mDrawerToggle;
    SharedPreferences sharedPref;
    String meetupSearchKeyword;
    String gitHubSearchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        fragmentManager = getSupportFragmentManager();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mainViewOption = bundle.getString(MAIN_KEY);
        }
        if (mainViewOption.equals(MEETUP_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.meetup_search_title));
            meetupSearchKeyword =
                    sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
            if (!meetupSearchKeyword.isEmpty()) {
                getMeetupFragment();
            } else {
                getSearchDialog();
            }
        } else if (mainViewOption.equals(GITHUB_MAIN_KEY)) {
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

    public void getSearchDialog() {
        if (mainViewOption.equals(MEETUP_MAIN_KEY)) {
            DialogFragment meetupDialogFragment = new MeetupDialogFragment();
            meetupDialogFragment.show(fragmentManager, "meetup_search");
        } else if (mainViewOption.equals(GITHUB_MAIN_KEY)) {
            DialogFragment gitHubDialogFragment = new GitHubDialogFragment();
            gitHubDialogFragment.show(fragmentManager, "github_search");
        }
    }

    public void getMeetupFragment() {
        meetupFragment = MeetupMainFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, meetupFragment).commit();
    }

    public void getGitHubFragment() {
        gitHubFragment = GitHubMainFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, gitHubFragment).commit();
    }

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

    @Override
    public void currentEventInfo(String groupUrl, String eventId) {
        MeetupDetailsFragment meetupDetailsFragment = MeetupDetailsFragment.newInstance(groupUrl,
                eventId);
        meetupDetailsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        meetupDetailsFragment.show(getSupportFragmentManager(), "meetup_details");
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
                if (!mainViewOption.equals(GITHUB_MAIN_KEY)) {
                    Intent gitHubIntent = new Intent(this, MainViewActivity.class);
                    gitHubIntent.putExtra(MAIN_KEY, GITHUB_MAIN_KEY);
                    startActivity(gitHubIntent);
                }
                break;
            case R.id.nav_meetup:
                if (!mainViewOption.equals(MEETUP_MAIN_KEY)) {
                    Intent meetupIntent = new Intent(this, MainViewActivity.class);
                    meetupIntent.putExtra(MAIN_KEY, MEETUP_MAIN_KEY);
                    startActivity(meetupIntent);
                }
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }
}
