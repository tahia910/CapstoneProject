package com.example.dailyupdate.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dailyupdate.BuildConfig;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.MeetupGroup;
import com.example.dailyupdate.networking.MeetupRetrofitInstance;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.ui.adapter.MeetupGroupAdapter;
import com.example.dailyupdate.ui.fragment.MeetupDialogFragment;
import com.example.dailyupdate.ui.fragment.MeetupMainFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetupMainViewActivity extends AppCompatActivity implements MeetupDialogFragment.MeetupDialogListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.meetup_main_recycler_view)
    RecyclerView recyclerView;

    MeetupMainFragment mainFragment;
    private FragmentManager fragmentManager;

    private String userLocation;
    private String defaultLocation = "tokyo";
    private int meetupGroupCategoryNumber = 34; // Category "Tech"
    private String API_KEY = BuildConfig.MEETUP_API_KEY;
    private String searchKeyword = "android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_main_drawer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("Meetup Search");
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showSearchDialog();
    }

    private void showSearchDialog() {
        DialogFragment newFragment = new MeetupDialogFragment();
        newFragment.show(getSupportFragmentManager(), "meetup_search");
    }

    private void retrieveMeetupGroups() {
        MeetupService meetupService =
                MeetupRetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);
        Call<List<MeetupGroup>> meetupGroupCall =
                meetupService.getMeetupGroupListWithKeywords(API_KEY, userLocation,
                        meetupGroupCategoryNumber, searchKeyword);
        meetupGroupCall.enqueue(new Callback<List<MeetupGroup>>() {
            @Override
            public void onResponse(Call<List<MeetupGroup>> call,
                                   Response<List<MeetupGroup>> response) {
                List<MeetupGroup> meetupGroupList = response.body();

                MeetupGroupAdapter meetupGroupAdapter =
                        new MeetupGroupAdapter(MeetupMainViewActivity.this, meetupGroupList, 2);
                recyclerView.setAdapter(meetupGroupAdapter);
            }
            @Override
            public void onFailure(Call<List<MeetupGroup>> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Bundle bundle) {
        String dialogKeyword = bundle.getString("KEYWORD");
        if (!dialogKeyword.isEmpty()) {
            searchKeyword = dialogKeyword;
        }
        retrieveMeetupGroups();
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
