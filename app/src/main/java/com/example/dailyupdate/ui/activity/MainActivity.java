package com.example.dailyupdate.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.GitHubRepo;
import com.example.dailyupdate.data.model.MeetupGroup;
import com.example.dailyupdate.ui.adapter.GitHubRepoAdapter;
import com.example.dailyupdate.ui.adapter.MeetupGroupAdapter;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.NetworkUtilities;
import com.example.dailyupdate.viewmodels.GitHubViewModel;
import com.example.dailyupdate.viewmodels.MeetupViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @BindView(R.id.home_github_recycler_view) RecyclerView githubRecyclerView;
    @BindView(R.id.home_meetup_recycler_view) RecyclerView meetupRecyclerView;
    @BindView(R.id.home_github_spinner) ProgressBar gitHubSpinner;
    @BindView(R.id.home_meetup_spinner) ProgressBar meetupSpinner;
    @BindView(R.id.home_github_emptyview) TextView gitHubEmptyView;
    @BindView(R.id.home_meetup_emptyview) TextView meetupEmptyView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout meetupRefreshLayout;


    private FusedLocationProviderClient mFusedLocationClient;
    private String userLocation;
    private SharedPreferences sharedPref;
    GitHubViewModel gitHubViewModel;
    GitHubRepoAdapter gitHubRepoAdapter;
    MeetupViewModel meetupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_drawer);
        ButterKnife.bind(this);
        setActionBar();
        setRecyclerViews();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        gitHubViewModel = ViewModelProviders.of(this).get(GitHubViewModel.class);
        meetupViewModel = ViewModelProviders.of(this).get(MeetupViewModel.class);
        subscribeGitHubObserver();
        subscribeMeetupGroupObserver();

        // Check if the network is available first, display empty view if there is no connection
        boolean isConnected = NetworkUtilities.checkNetworkAvailability(this);
        if (!isConnected) {
            setEmptyViewNoNetworkAvailable();
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // Check location permission before retrieving the Meetup groups
            checkLocationPermission();

            // Retrieve GitHub repositories
            gitHubViewModel.searchGitHubRepoList(Constants.GITHUB_DEFAULT_SEARCH_KEYWORD,
                    Constants.GITHUB_DEFAULT_SORT_ORDER);
        }

        // Set the swipe action on Meetup events list to refresh the search
        meetupRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLocationPermission();
                meetupRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Confirm the location information before retrieving the Meetup groups nearby the user or
     * near the default location (Tokyo)
     **/
    private void retrieveMeetupGroups() {
        boolean locationSwitchValue = sharedPref.getBoolean(getString(R.string.pref_location_key)
                , false);
        if (locationSwitchValue) {
            userLocation = sharedPref.getString(getString(R.string.pref_meetup_location_key), "");
        } else {
            Toast.makeText(this, R.string.location_not_retrieved, Toast.LENGTH_LONG).show();
            userLocation = Constants.DEFAULT_LOCATION;
        }
        // Retrieve the Meetup groups based on the criterias above
        meetupViewModel.searchMeetupGroups(userLocation, Constants.MEETUP_TECH_CATEGORY_NUMBER,
                Constants.MEETUP_GROUP_RESPONSE_PAGE);
    }

    /**
     * Set the empty views if there is no network available
     **/
    private void setEmptyViewNoNetworkAvailable() {
        gitHubSpinner.setVisibility(View.GONE);
        meetupSpinner.setVisibility(View.GONE);
        gitHubEmptyView.setVisibility(View.VISIBLE);
        gitHubEmptyView.setText(R.string.no_internet_connection);
        meetupEmptyView.setVisibility(View.VISIBLE);
        meetupEmptyView.setText(R.string.no_internet_connection);
    }

    private void subscribeGitHubObserver() {
        gitHubViewModel.getGitHubRepoList().observe(this, new Observer<List<GitHubRepo>>() {
            @Override
            public void onChanged(List<GitHubRepo> gitHubRepoList) {
                if (gitHubRepoList != null) {
                    gitHubSpinner.setVisibility(View.GONE);
                    gitHubRepoAdapter = new GitHubRepoAdapter(MainActivity.this, gitHubRepoList, 1);
                    githubRecyclerView.setAdapter(gitHubRepoAdapter);

                    gitHubRepoAdapter.setOnItemClickListener((position, v) -> {
                        GitHubRepo gitHubRepo = gitHubRepoList.get(position);
                        String gitHubRepoUrl = gitHubRepo.getHtmlUrl();
                        NetworkUtilities.openCustomTabs(getApplicationContext(), gitHubRepoUrl);
                    });
                }
            }
        });
    }

    private void subscribeMeetupGroupObserver() {
        meetupViewModel.getMeetupGroupList().observe(this, new Observer<List<MeetupGroup>>() {
            @Override
            public void onChanged(List<MeetupGroup> meetupGroupList) {
                if (meetupGroupList != null) {
                    meetupSpinner.setVisibility(View.GONE);
                    MeetupGroupAdapter meetupGroupAdapter =
                            new MeetupGroupAdapter(MainActivity.this, meetupGroupList);
                    meetupRecyclerView.setAdapter(meetupGroupAdapter);

                    // Use an intent to open a browser and display the group details
                    meetupGroupAdapter.setOnItemClickListener((position, v) -> {
                        MeetupGroup meetupGroup = meetupGroupList.get(position);
                        String groupUrlString = meetupGroup.getGroupUrl();
                        Uri groupUrl = Uri.parse(groupUrlString);
                        startActivity(new Intent(Intent.ACTION_VIEW, groupUrl));
                    });
                }
            }
        });
    }

    /**
     * Set up the toolbar and drawer
     **/
    private void setActionBar() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    /**
     * Set up the spinners and recycler views
     **/
    private void setRecyclerViews() {
        gitHubSpinner.setVisibility(View.VISIBLE);
        meetupSpinner.setVisibility(View.VISIBLE);
        githubRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        meetupRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }


    /**
     * Get the device current location (city accuracy)
     **/
    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        LocationRequest locationRequest =
                new LocationRequest().setInterval(Constants.LOCATION_UPDATE_INTERVAL).setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double currentLongitude = location.getLongitude();
                double currentLatitude = location.getLatitude();
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    // Request city level accuracy
                    List<Address> address = gcd.getFromLocation(currentLatitude, currentLongitude
                            , 1);
                    userLocation = address.get(0).getLocality();

                    // Update the user location in the preferences
                    sharedPref.edit().putString(getString(R.string.pref_meetup_location_key),
                            userLocation).apply();
                    retrieveMeetupGroups();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Could not retrieve the location
                // Default location will be used
                retrieveMeetupGroups();
            }
        });
    }

    /**
     * Check if the location permission was granted in order to display trending Meetup tech
     * groups near the user
     **/
    @TargetApi(Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ACCESS_COARSE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_COARSE_LOCATION);
        } else {
            // Permission is already granted
            getLocation();
        }
    }

    /**
     * Get the location permission result
     **/
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS_REQUEST_COARSE_LOCATION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        // Location permission was granted this time
                        // Update the location switch in the preferences
                        sharedPref.edit().putBoolean(getString(R.string.pref_location_key), true).apply();
                        getLocation();
                    } else {
                        // Permission was not granted
                        // Use default location (Tokyo)
                        Toast.makeText(this, R.string.message_default_location,
                                Toast.LENGTH_LONG).show();
                        retrieveMeetupGroups();
                    }
                }
            }
        }

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
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            selectDrawerItem(menuItem);
            return true;
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_bookmarks:
                startActivity(new Intent(this, BookmarksActivity.class));
                break;
            case R.id.nav_github:
                Intent gitHubIntent = new Intent(MainActivity
                        .this, MainViewActivity.class);
                gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY);
                startActivity(gitHubIntent);
                break;
            case R.id.nav_meetup:
                Intent meetupIntent = new Intent(MainActivity
                        .this, MainViewActivity.class);
                meetupIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY);
                startActivity(meetupIntent);
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }

}
