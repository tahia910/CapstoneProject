package com.example.dailyupdate.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragments.GitHubMainFragment;
import com.example.dailyupdate.ui.fragments.MeetupMainFragment;
import com.example.dailyupdate.ui.fragments.dialogs.GitHubDialogFragment;
import com.example.dailyupdate.ui.fragments.dialogs.MeetupDetailsFragment;
import com.example.dailyupdate.ui.fragments.dialogs.MeetupDialogFragment;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.NetworkUtilities;
import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener, GitHubDialogFragment.GitHubDialogListener, MeetupMainFragment.MeetupMainFragmentListener, MeetupDetailsFragment.MeetupDetailsFragmentListener {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.appbar_layout_emptyview) TextView emptyView;

    private FragmentManager fragmentManager;
    private MeetupDetailsFragment meetupDetailsFragment;
    private String mainViewOption;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences sharedPref;
    private String sharedPrefMeetupSearchKeyword;
    private String sharedPrefGitHubSearchKeyword;
    private ActionBar ab;
    private String gitHubDialogLatestSearchKeyword;
    private String gitHubDialogLatestSortBy;
    private String gitHubDialogLatestSearchOrder;
    private String meetupDialogLatestSearchKeyword;
    private String meetupDialogLatestSortBy;
    private String meetupDialogLatestLocation;

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
        if (savedInstanceState != null) {
            if (mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
                meetupDialogLatestSearchKeyword =
                        savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD);
                meetupDialogLatestSortBy =
                        savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_SORT);
                meetupDialogLatestLocation =
                        savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_LOCATION);
            } else if (mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
                gitHubDialogLatestSearchKeyword =
                        savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD);
                gitHubDialogLatestSortBy =
                        savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_SORT);
                gitHubDialogLatestSearchOrder =
                        savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_ORDER);
            }
        }
        openDialogOrFragment(savedInstanceState);
    }

    /**
     * Decide which fragment or dialog to display from this activity.
     **/
    private void openDialogOrFragment(Bundle savedInstanceState) {
        if (mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.meetup_search_title));
            ViewModelProviders.of(MainViewActivity.this).get(BookmarksDatabaseViewModel.class);
            sharedPrefMeetupSearchKeyword =
                    sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
            // If the search keyword is empty, display the search dialog
            if (!sharedPrefMeetupSearchKeyword.isEmpty()) {
                // If the savedInstanceState is not null, the previous fragment will restore
                // itself automatically, so do not add another one on top
                if (savedInstanceState == null){
                    getMeetupFragment();
                }
            } else {
                getSearchDialog();
            }
        } else if (mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.github_search_title));
            sharedPrefGitHubSearchKeyword =
                    sharedPref.getString(getString(R.string.pref_github_edittext_key), "");
            if (!sharedPrefGitHubSearchKeyword.isEmpty()) {
                if (savedInstanceState == null) {
                    getGitHubFragment();
                }
            } else {
                getSearchDialog();
            }
        }
    }

    /**
     * Set up the toolbar and drawer.
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

    private void setEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(getString(R.string.no_search_set_emptyview_message));
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // If there is any data from a dialog's onSaveInstanceState(), save them here (in the
        // parent activity) to retrieve them when the activity is restored and launches the
        // dialog again
        if (gitHubDialogLatestSearchKeyword != null || gitHubDialogLatestSortBy != null || gitHubDialogLatestSearchOrder != null) {
            outState.putString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD,
                    gitHubDialogLatestSearchKeyword);
            outState.putString(Constants.KEY_GITHUB_DIALOG_SORT, gitHubDialogLatestSortBy);
            outState.putString(Constants.KEY_GITHUB_DIALOG_ORDER, gitHubDialogLatestSearchOrder);
        }
        if (meetupDialogLatestSearchKeyword != null || meetupDialogLatestSortBy != null || meetupDialogLatestLocation != null) {
            outState.putString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD,
                    meetupDialogLatestSearchKeyword);
            outState.putString(Constants.KEY_MEETUP_DIALOG_SORT, meetupDialogLatestSortBy);
            outState.putString(Constants.KEY_MEETUP_DIALOG_LOCATION, meetupDialogLatestLocation);
        }
    }

    /**
     * Get the search dialog for either Meetup events or GitHub repositories, set as arguments
     * the values previously inputted and saved in the dialog's onSaveInstanceState().
     * (The null check for each values will be done before setting them back in the dialog's
     * setSavedInstanceStateValues() method.)
     **/
    private void getSearchDialog() {
        if (mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
            DialogFragment meetupDialogFragment = new MeetupDialogFragment();
            Bundle args = new Bundle();
            args.putString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD,
                    meetupDialogLatestSearchKeyword);
            args.putString(Constants.KEY_MEETUP_DIALOG_SORT, meetupDialogLatestSortBy);
            args.putString(Constants.KEY_MEETUP_DIALOG_LOCATION, meetupDialogLatestLocation);
            meetupDialogFragment.setArguments(args);
            meetupDialogFragment.setEnterTransition(R.anim.fade_in);
            meetupDialogFragment.setExitTransition(R.anim.fade_out);
            meetupDialogFragment.show(fragmentManager, "meetup_search");

        } else if (mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
            DialogFragment gitHubDialogFragment = new GitHubDialogFragment();
            Bundle args = new Bundle();
            args.putString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD,
                    gitHubDialogLatestSearchKeyword);
            args.putString(Constants.KEY_GITHUB_DIALOG_SORT, gitHubDialogLatestSortBy);
            args.putString(Constants.KEY_GITHUB_DIALOG_ORDER, gitHubDialogLatestSearchOrder);
            gitHubDialogFragment.setArguments(args);
            gitHubDialogFragment.setEnterTransition(R.anim.fade_in);
            gitHubDialogFragment.setExitTransition(R.anim.fade_out);
            gitHubDialogFragment.show(fragmentManager, "github_search");
        }
    }

    private void getMeetupFragment() {
        MeetupMainFragment meetupFragment = MeetupMainFragment.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, meetupFragment).commit();
    }

    private void getGitHubFragment() {
        GitHubMainFragment gitHubFragment = GitHubMainFragment.Companion.newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_container, gitHubFragment).commit();
    }

    /**
     * Callback from MeetupDialogFragment (Meetup events search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button.
     **/
    @Override
    public void onMeetupDialogPositiveClick(DialogFragment dialog) {
        sharedPrefMeetupSearchKeyword =
                sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
        if (!sharedPrefMeetupSearchKeyword.isEmpty()) {
            getMeetupFragment();
        } else {
            setEmptyView();
            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMeetupDialogNegativeClick(DialogFragment dialog) {
        setEmptyView();
        Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Callback from the Meetup event search dialog, sending the user latest input before
     * the dialog was destroyed.
     **/
    @Override
    public void restoreMeetupDialogState(Bundle bundle) {
        meetupDialogLatestSearchKeyword =
                bundle.getString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD);
        meetupDialogLatestSortBy = bundle.getString(Constants.KEY_MEETUP_DIALOG_SORT);
        meetupDialogLatestLocation = bundle.getString(Constants.KEY_MEETUP_DIALOG_LOCATION);
    }

    /**
     * Callback from GitHubDialogFragment (GitHub search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button.
     **/
    @Override
    public void onGitHubDialogPositiveClick(DialogFragment dialog) {
        sharedPrefGitHubSearchKeyword =
                sharedPref.getString(getString(R.string.pref_github_edittext_key), "");
        if (!sharedPrefGitHubSearchKeyword.isEmpty()) {
            getGitHubFragment();
        } else {
            setEmptyView();
            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGitHubDialogNegativeClick(DialogFragment dialog) {
        setEmptyView();
        Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Callback from the GitHub repository search dialog, sending the user latest input before
     * the dialog was destroyed.
     **/
    @Override
    public void restoreGitHubDialogState(Bundle bundle) {
        gitHubDialogLatestSearchKeyword =
                bundle.getString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD);
        gitHubDialogLatestSortBy = bundle.getString(Constants.KEY_GITHUB_DIALOG_SORT);
        gitHubDialogLatestSearchOrder = bundle.getString(Constants.KEY_GITHUB_DIALOG_ORDER);
    }

    /**
     * Callback from MeetupMainFragment (fragment displaying the result of a Meetup event search)
     * to open the details of a selected event in a dialog.
     * Check if the fragment is already displayed just in case.
     **/
    @Override
    public void currentEventInfo(String groupUrl, String eventId) {
        if (fragmentManager.findFragmentByTag(Constants.TAG_EVENT_DETAILS_FRAGMENT) != null) {
            meetupDetailsFragment.dismiss();
        }
        meetupDetailsFragment = MeetupDetailsFragment.newInstance(groupUrl, eventId);
        meetupDetailsFragment.setHasOptionsMenu(true);
        meetupDetailsFragment.setMenuVisibility(true);
        meetupDetailsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        meetupDetailsFragment.show(fragmentManager, Constants.TAG_EVENT_DETAILS_FRAGMENT);
    }

    /**
     * MeetupDetailsFragment callback used for the BookmarksActivity, where there is a
     * possibility that the fragment gets displayed twice due to the widget.
     **/
    @Override
    public void closedFragmentCallback() {
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
                emptyView.setVisibility(View.GONE);
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
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.nav_bookmarks:
                Intent bookmarkIntent = new Intent(this, BookmarksActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                    startActivity(bookmarkIntent, bundle);
                } else {
                    startActivity(bookmarkIntent);
                }
                break;
            case R.id.nav_github:
                if (!mainViewOption.equals(Constants.GITHUB_MAIN_KEY)) {
                    Intent gitHubIntent = new Intent(this, MainViewActivity.class);
                    gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                        startActivity(gitHubIntent, bundle);
                    } else {
                        startActivity(gitHubIntent);
                    }
                }
                break;
            case R.id.nav_meetup:
                if (!mainViewOption.equals(Constants.MEETUP_MAIN_KEY)) {
                    Intent meetupIntent = new Intent(this, MainViewActivity.class);
                    meetupIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                        startActivity(meetupIntent, bundle);
                    } else {
                        startActivity(meetupIntent);
                    }
                }
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }

}
