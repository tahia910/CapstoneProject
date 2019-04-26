package com.example.dailyupdate.ui;

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
import androidx.fragment.app.FragmentManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragment.BookmarksFragment;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dailyupdate.ui.MainActivity.GITHUB_MAIN_KEY;
import static com.example.dailyupdate.ui.MainActivity.MAIN_KEY;
import static com.example.dailyupdate.ui.MainActivity.MEETUP_MAIN_KEY;

public class BookmarksActivity extends AppCompatActivity {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    BookmarksFragment bookmarksFragment;

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
        bookmarksFragment = BookmarksFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, bookmarksFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general_menu, menu);
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
                gitHubIntent.putExtra(MAIN_KEY, GITHUB_MAIN_KEY);
                startActivity(gitHubIntent);
                break;
            case R.id.nav_meetup:
                Intent meetupIntent = new Intent(this, MainViewActivity.class);
                meetupIntent.putExtra(MAIN_KEY, MEETUP_MAIN_KEY);
                startActivity(meetupIntent);
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }
}
