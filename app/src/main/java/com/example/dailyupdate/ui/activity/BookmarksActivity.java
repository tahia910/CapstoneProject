package com.example.dailyupdate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragment.BookmarksFragment;
import com.example.dailyupdate.ui.fragment.MeetupDetailsFragment;
import com.example.dailyupdate.utilities.Constants;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity implements BookmarksFragment.BookmarksFragmentListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    private BookmarksFragment bookmarksFragment;

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

        // TODO: handle the case the user presses "back" when coming from widget
        // & Use broadcast/jobservice instead?
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_GROUP_URL)) {
            // If the intent is not empty, it means that the activity was started from the widget to
            // display a specific bookmarked event's details.
            String groupUrl = intent.getStringExtra(Constants.EXTRA_GROUP_URL);
            String eventId = intent.getStringExtra(Constants.EXTRA_EVENT_ID);
            displayEventDetails(groupUrl, eventId);
        } else {
            fragmentManager = getSupportFragmentManager();
            bookmarksFragment = BookmarksFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    bookmarksFragment).commit();
        }
    }

    @Override
    public void displayEventDetails(String groupUrl, String eventId) {
        MeetupDetailsFragment meetupDetailsFragment = MeetupDetailsFragment.newInstance(groupUrl,
                eventId);
        meetupDetailsFragment.setHasOptionsMenu(true);
        meetupDetailsFragment.setMenuVisibility(true);
        meetupDetailsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        meetupDetailsFragment.show(getSupportFragmentManager(), "meetup_details");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmarks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_pref:
                Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.action_delete_all:
                BookmarksDatabaseViewModel viewModel = ViewModelProviders.of(this).get(BookmarksDatabaseViewModel.class);
                viewModel.deleteAllBookmarkedEvent();
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
                break;
            case R.id.nav_github:
                Intent gitHubIntent = new Intent(this, MainViewActivity.class);
                gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY);
                startActivity(gitHubIntent);
                break;
            case R.id.nav_meetup:
                Intent meetupIntent = new Intent(this, MainViewActivity.class);
                meetupIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY);
                startActivity(meetupIntent);
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }
}
