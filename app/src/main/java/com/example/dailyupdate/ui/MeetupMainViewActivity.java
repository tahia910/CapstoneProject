package com.example.dailyupdate.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.fragment.MeetupDialogFragment;
import com.example.dailyupdate.ui.fragment.MeetupMainFragment;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupMainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    MeetupMainFragment mainFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("Meetup Search");
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        fragmentManager = getSupportFragmentManager();
        showSearchDialog();
    }

    private void showSearchDialog() {
        DialogFragment newFragment = new MeetupDialogFragment();
        newFragment.show(getSupportFragmentManager(), "meetup_search");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle bundle) {
        // TODO: check if bundle is empty?
        mainFragment = MeetupMainFragment.newInstance(bundle);
        fragmentManager.beginTransaction().add(R.id.fragment_container, mainFragment).commit();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // TODO: handle dialog cancel option(2)
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
