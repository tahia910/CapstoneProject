package com.example.dailyupdate;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dailyupdate.data.GitHubRepo;
import com.example.dailyupdate.data.GitHubResponse;
import com.example.dailyupdate.data.MeetupGroup;
import com.example.dailyupdate.networking.GitHubRetrofitInstance;
import com.example.dailyupdate.networking.GitHubService;
import com.example.dailyupdate.networking.MeetupRetrofitInstance;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.ui.GitHubRepoAdapter;
import com.example.dailyupdate.ui.MeetupGroupAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    public static final String TAG = MainActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        //TODO: Check if connected to internet

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();

        retrieveGithubRepo();
    }

    private void retrieveGithubRepo() {
        GitHubService service =
                GitHubRetrofitInstance.getRetrofitInstance().create(GitHubService.class);
        Call<GitHubResponse> repoListCall = service.getGitHubRepoList(gitHubDefaultSearchKeyword, gitHubDefaultSortOrder);
        repoListCall.enqueue(new Callback<GitHubResponse>() {
            @Override
            public void onResponse(Call<GitHubResponse> call, Response<GitHubResponse> response) {
                gitHubSpinner.setVisibility(View.GONE);
                GitHubResponse gitHubResponse = response.body();
                List<GitHubRepo> gitHubRepoList = gitHubResponse.getGitHubRepo();
                GitHubRepoAdapter gitHubRepoAdapter = new GitHubRepoAdapter(MainActivity.this,
                        gitHubRepoList);
                githubRecyclerView.setAdapter(gitHubRepoAdapter);
            }

            @Override
            public void onFailure(Call<GitHubResponse> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

    private void retrieveMeetupGroups() {
        MeetupService meetupService =
                MeetupRetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);
        Call<List<MeetupGroup>> meetupGroupCall = meetupService.getMeetupGroupList(API_KEY
                , userLocation, meetupGroupCategoryNumber, meetupGroupResponsePageNumber);
        meetupGroupCall.enqueue(new Callback<List<MeetupGroup>>() {
            @Override
            public void onResponse(Call<List<MeetupGroup>> call,
                                   Response<List<MeetupGroup>> response) {
                List<MeetupGroup> meetupGroupList = response.body();
                MeetupGroupAdapter meetupGroupAdapter = new MeetupGroupAdapter(MainActivity.this,
                        meetupGroupList);
                meetupRecyclerView.setAdapter(meetupGroupAdapter);
            }

            @Override
            public void onFailure(Call<List<MeetupGroup>> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }


    /**
     * Request city level accuracy
     **/
    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double currentLongitude = location.getLongitude();
                double currentLatitude = location.getLatitude();
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> address = gcd.getFromLocation(currentLatitude, currentLongitude
                            , 1);
                    userLocation = address.get(0).getLocality();
                    retrieveMeetupGroups();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,
                        R.string.location_not_retrieved, Toast.LENGTH_LONG).show();
                userLocation = defaultLocation;
                retrieveMeetupGroups();
            }
        });
    }

    /**
     * Ask for location permission in order to display trending Meetup events near the user
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Ask permission
            if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
            } else {
                getLocation();
            }
            //TODO : handle "never ask again" case
        }
    }

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
                        getLocation();
                    } else {
                        Toast.makeText(this, R.string.message_default_location,
                                Toast.LENGTH_LONG).show();
                        userLocation = defaultLocation;
                        retrieveMeetupGroups();
                    }
                }
            }
        }
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
