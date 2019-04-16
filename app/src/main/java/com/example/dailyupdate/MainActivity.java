package com.example.dailyupdate;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dailyupdate.data.GitHubRepo;
import com.example.dailyupdate.data.GitHubResponse;
import com.example.dailyupdate.networking.GitHubService;
import com.example.dailyupdate.networking.RetrofitClientInstance;
import com.example.dailyupdate.ui.GitHubRepoAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 111;
    private static final long LOCATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;
    private LocationSettingsRequest mLocationSettingsRequest;

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

        // Check if connected to internet

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        checkPermission();
//        setLocation();
        // Set up the recycler views.

        gitHubSpinner.setVisibility(View.VISIBLE);
        githubRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        GitHubService service =
                RetrofitClientInstance.getRetrofitInstance().create(GitHubService.class);
        Call<GitHubResponse> repoListCall = service.getGitHubRepoList("android", "updated");
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

            }
        });
    }

    private void setLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the user location.
            getLocation();
        } else {
            // The Meetup search default location will be Tokyo.
            // Display a toast explaining the situation to user.
            Toast.makeText(this, "The default location will be Tokyo", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        // Request city level accuracy
        LocationRequest locationRequest =
                new LocationRequest().setInterval(LOCATION_UPDATE_INTERVAL).setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Do something with the location
                }
            }
        });
    }

    /**
     * Ask for location permission in order to display trending Meetup events near the user
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
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
