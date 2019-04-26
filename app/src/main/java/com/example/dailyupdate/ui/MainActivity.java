package com.example.dailyupdate.ui;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.BuildConfig;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.GitHubRepo;
import com.example.dailyupdate.data.GitHubResponse;
import com.example.dailyupdate.data.MeetupGroup;
import com.example.dailyupdate.networking.GitHubService;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.ui.adapter.GitHubRepoAdapter;
import com.example.dailyupdate.ui.adapter.MeetupGroupAdapter;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.github_recycler_view)
    RecyclerView githubRecyclerView;
    @BindView(R.id.github_spinner)
    ProgressBar gitHubSpinner;
    @BindView(R.id.meetup_recycler_view)
    RecyclerView meetupRecyclerView;

    public static final String MAIN_KEY = "mainKey";
    public static final String MEETUP_MAIN_KEY = "meetupMainKey";
    public static final String GITHUB_MAIN_KEY = "gitHubMainKey";

    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 111;
    private static final long LOCATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours
    private FusedLocationProviderClient mFusedLocationClient;
    private String gitHubDefaultSearchKeyword = "android";
    private String gitHubDefaultSortOrder = "updated";
    private String userLocation;
    private String defaultLocation = "tokyo";
    private int meetupGroupCategoryNumber = 34; // Category "Tech"
    private int meetupGroupResponsePageNumber = 20;
    private String API_KEY = BuildConfig.MEETUP_API_KEY;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_drawer);
        ButterKnife.bind(this);

        // Set up the toolbar and drawer
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Set up the recycler views.
        gitHubSpinner.setVisibility(View.VISIBLE);
        githubRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        meetupRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //TODO: Check if connected to internet

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission();

        retrieveGithubRepo();
    }

    private void retrieveGithubRepo() {
        GitHubService service =
                RetrofitInstance.getGitHubRetrofitInstance().create(GitHubService.class);
        Call<GitHubResponse> repoListCall = service.getGitHubRepoList(gitHubDefaultSearchKeyword,
                gitHubDefaultSortOrder);
        repoListCall.enqueue(new Callback<GitHubResponse>() {
            @Override
            public void onResponse(Call<GitHubResponse> call, Response<GitHubResponse> response) {
                gitHubSpinner.setVisibility(View.GONE);
                GitHubResponse gitHubResponse = response.body();
                List<GitHubRepo> gitHubRepoList = gitHubResponse.getGitHubRepo();
                GitHubRepoAdapter gitHubRepoAdapter = new GitHubRepoAdapter(MainActivity.this,
                        gitHubRepoList, 1);
                githubRecyclerView.setAdapter(gitHubRepoAdapter);
            }

            @Override
            public void onFailure(Call<GitHubResponse> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

    private void retrieveMeetupGroups() {
        boolean locationSwitchValue = sharedPref.getBoolean(getString(R.string.pref_location_key)
                , false);
        if (locationSwitchValue) {
            userLocation = sharedPref.getString(getString(R.string.pref_meetup_location_key), "");
        } else {
            Toast.makeText(this, R.string.location_not_retrieved, Toast.LENGTH_LONG).show();
            userLocation = defaultLocation;
        }
        MeetupService meetupService =
                RetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);
        Call<List<MeetupGroup>> meetupGroupCall = meetupService.getMeetupGroupList(API_KEY,
                userLocation, meetupGroupCategoryNumber, meetupGroupResponsePageNumber);
        meetupGroupCall.enqueue(new Callback<List<MeetupGroup>>() {
            @Override
            public void onResponse(Call<List<MeetupGroup>> call,
                                   Response<List<MeetupGroup>> response) {
                List<MeetupGroup> meetupGroupList = response.body();
                MeetupGroupAdapter meetupGroupAdapter = new MeetupGroupAdapter(MainActivity.this,
                        meetupGroupList);
                meetupRecyclerView.setAdapter(meetupGroupAdapter);

                // Use an intent to open a browser and display the group details
                meetupGroupAdapter.setOnItemClickListener((position, v) -> {
                    MeetupGroup meetupGroup = meetupGroupList.get(position);
                    String groupUrlString = meetupGroup.getGroupUrl();
                    Uri groupUrl = Uri.parse(groupUrlString);
                    startActivity(new Intent(Intent.ACTION_VIEW, groupUrl));
                });
            }

            @Override
            public void onFailure(Call<List<MeetupGroup>> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }


    /**
     * Get the device current location (city accuracy)
     **/
    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        LocationRequest locationRequest =
                new LocationRequest().setInterval(LOCATION_UPDATE_INTERVAL).setPriority(LocationRequest.PRIORITY_LOW_POWER);
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
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
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

        if (requestCode == PERMISSIONS_REQUEST_COARSE_LOCATION) {
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                selectDrawerItem(menuItem);
                return true;
            }
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
                gitHubIntent.putExtra(MAIN_KEY, GITHUB_MAIN_KEY);
                startActivity(gitHubIntent);
                break;
            case R.id.nav_meetup:
                Intent meetupIntent = new Intent(MainActivity
                        .this, MainViewActivity.class);
                meetupIntent.putExtra(MAIN_KEY, MEETUP_MAIN_KEY);
                startActivity(meetupIntent);
                break;
            default:
                break;
        }
        mDrawer.closeDrawers();
    }

}
