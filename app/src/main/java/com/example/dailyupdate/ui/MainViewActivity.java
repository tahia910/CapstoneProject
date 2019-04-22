package com.example.dailyupdate.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragment.GitHubDialogFragment;
import com.example.dailyupdate.ui.fragment.GitHubMainFragment;
import com.example.dailyupdate.ui.fragment.MeetupDialogFragment;
import com.example.dailyupdate.ui.fragment.MeetupMainFragment;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dailyupdate.MainActivity.GITHUB_MAIN_KEY;
import static com.example.dailyupdate.MainActivity.MAIN_KEY;
import static com.example.dailyupdate.MainActivity.MEETUP_MAIN_KEY;

public class MainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener, GitHubDialogFragment.GitHubDialogListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    MeetupMainFragment meetupFragment;
    GitHubMainFragment gitHubFragment;
    private FragmentManager fragmentManager;
    private String mainViewOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        fragmentManager = getSupportFragmentManager();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mainViewOption = bundle.getString(MAIN_KEY);
        }
        if (mainViewOption.equals(MEETUP_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.meetup_search_title));
            DialogFragment meetupDialogFragment = new MeetupDialogFragment();
            meetupDialogFragment.show(getSupportFragmentManager(), "meetup_search");

        } else if (mainViewOption.equals(GITHUB_MAIN_KEY)) {
            ab.setTitle(getApplicationContext().getString(R.string.github_search_title));
            DialogFragment gitHubDialogFragment = new GitHubDialogFragment();
            gitHubDialogFragment.show(getSupportFragmentManager(), "github_search");
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle bundle) {
        // TODO: check if bundle is empty?
        meetupFragment = MeetupMainFragment.newInstance(bundle);
        fragmentManager.beginTransaction().add(R.id.fragment_container, meetupFragment).commit();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // TODO: handle meetup dialog cancel option(2)
    }

    @Override
    public void onGitHubDialogPositiveClick(DialogFragment dialog, Bundle bundle) {
        gitHubFragment = GitHubMainFragment.newInstance(bundle);
        fragmentManager.beginTransaction().add(R.id.fragment_container, gitHubFragment).commit();

    }

    @Override
    public void onGitHubDialogNegativeClick(DialogFragment dialog) {
        // TODO: handle github dialog cancel option(2)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            selectDrawerItem(menuItem);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_bookmarks:
                Toast.makeText(this, "Bookmarks", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_github:
                Toast.makeText(this, "Github", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_meetup:
                Toast.makeText(this, "Meetup", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }
}
