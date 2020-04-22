package com.example.dailyupdate.ui.activities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyupdate.R
import com.example.dailyupdate.data.models.MeetupGroup
import com.example.dailyupdate.ui.adapters.GitHubRepoAdapter
import com.example.dailyupdate.ui.adapters.MeetupGroupAdapter
import com.example.dailyupdate.utilities.Constants
import com.example.dailyupdate.utilities.NetworkUtilities
import com.example.dailyupdate.utilities.Status
import com.example.dailyupdate.viewmodels.GitHubViewModel
import com.example.dailyupdate.viewmodels.MeetupViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.home_drawer.*
import kotlinx.android.synthetic.main.home_layout.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var userLocation: String? = null
    private var sharedPref: SharedPreferences? = null
    private lateinit var gitHubViewModel: GitHubViewModel
    private lateinit var meetupViewModel: MeetupViewModel
    private var gitHubRecyclerViewLastPosition = 0
    private var gitHubRecyclerViewOption = 0
    private var meetupRecyclerViewLastPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_drawer)
        setActionBar()
        // Check if there was a screen rotation and set up the RecyclerViews
        setRecyclerViews(savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        gitHubViewModel = ViewModelProvider(this).get(GitHubViewModel::class.java)
        meetupViewModel = ViewModelProvider(this).get(MeetupViewModel::class.java)
        subscribeGitHubObserver()
        subscribeMeetupGroupObserver()

        // Check if the network is available first, display empty view if there is no connection
        val isConnected = NetworkUtilities.checkNetworkAvailability(this)
        if (!isConnected) {
            setEmptyViewNoNetworkAvailable()
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            // Check location permission before retrieving the Meetup groups
            checkLocationPermission()

            // Retrieve GitHub repositories
            gitHubViewModel.searchGitHubRepoList(Constants.GITHUB_DEFAULT_SEARCH_KEYWORD,
                    Constants.GITHUB_DEFAULT_SORT_ORDER)

            //Initialize the Mobile Ads SDK with AdMob App ID (sample ID for test ads)
            MobileAds.initialize(this, getString(R.string.admob_app_sample_id))
        }
    }

    /**
     * Confirm the location information before retrieving the Meetup groups nearby the user or
     * near the default location (Tokyo)
     */
    private fun retrieveMeetupGroups() {
        val locationSwitchValue = sharedPref!!.getBoolean(getString(R.string.pref_location_key), false)
        userLocation = if (locationSwitchValue) {
            sharedPref!!.getString(getString(R.string.pref_meetup_location_key), "")
        } else {
            Toast.makeText(this, R.string.location_not_retrieved, Toast.LENGTH_LONG).show()
            Constants.DEFAULT_LOCATION
        }
        // Retrieve the Meetup groups based on the criterias above
        meetupViewModel.searchMeetupGroups(userLocation, Constants.MEETUP_TECH_CATEGORY_NUMBER,
                Constants.MEETUP_GROUP_RESPONSE_PAGE)
    }

    /**
     * Set the empty views if there is no network available
     */
    private fun setEmptyViewNoNetworkAvailable() {
        home_github_spinner.visibility = View.GONE
        home_meetup_spinner.visibility = View.GONE
        home_github_emptyview.visibility = View.VISIBLE
        home_github_emptyview.setText(R.string.no_internet_connection)
        home_meetup_emptyview.visibility = View.VISIBLE
        home_meetup_emptyview.setText(R.string.no_internet_connection)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        gitHubRecyclerViewLastPosition = (home_github_recycler_view.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        outState.putInt(Constants.KEY_GITHUB_RECYCLERVIEW_POSITION, gitHubRecyclerViewLastPosition)
        outState.putInt(Constants.KEY_GITHUB_RECYCLERVIEW_OPTION, gitHubRecyclerViewOption)
        meetupRecyclerViewLastPosition = (home_meetup_recycler_view.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
        outState.putInt(Constants.KEY_MEETUP_RECYCLERVIEW_POSITION, meetupRecyclerViewLastPosition)
    }

    private fun subscribeGitHubObserver() {
        gitHubViewModel.gitHubRepoList.observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        home_github_spinner.visibility = View.GONE
                        val adapter = GitHubRepoAdapter()
                        adapter.setGitHubItemList(it.data, 1)
                        home_github_recycler_view.adapter = adapter

                        // If there was a screen rotation, restore the previous position
                        if (gitHubRecyclerViewLastPosition != 0) {
                            if (gitHubRecyclerViewOption == 1) {
                                (home_github_recycler_view.layoutManager as LinearLayoutManager?)!!.scrollToPosition(gitHubRecyclerViewLastPosition)
                            } else {
                                (home_github_recycler_view.layoutManager as GridLayoutManager?)!!.scrollToPosition(gitHubRecyclerViewLastPosition)
                            }
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                }
            }
        })
    }

    private fun subscribeMeetupGroupObserver() {
        meetupViewModel.meetupGroupList.observe(this, Observer { meetupGroupList: List<MeetupGroup>? ->
            if (meetupGroupList != null) {
                home_meetup_spinner.visibility = View.GONE
                val meetupGroupAdapter = MeetupGroupAdapter(this@MainActivity, meetupGroupList)
                home_meetup_recycler_view.adapter = meetupGroupAdapter

                // If there was a screen rotation, restore the previous position
                if (meetupRecyclerViewLastPosition != 0) {
                    (home_meetup_recycler_view.layoutManager as GridLayoutManager?)!!.scrollToPosition(meetupRecyclerViewLastPosition)
                }

                // Display the group details using WebView(Custom Tabs)
                meetupGroupAdapter.setOnItemClickListener { position: Int, v: View? ->
                    val meetupGroup: MeetupGroup = meetupGroupList.get(position)
                    val groupUrlString: String = meetupGroup.groupUrl
                    NetworkUtilities.openCustomTabs(this@MainActivity, groupUrlString)
                }

                // Set the swipe action on Meetup events list to refresh the search
                swipe_refresh_layout.setOnRefreshListener {
                    checkLocationPermission()
                    swipe_refresh_layout.isRefreshing = false
                }
            }
        })
    }

    /**
     * Set up the toolbar and drawer
     */
    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (nav_view != null) setupDrawerContent(nav_view)
    }

    /**
     * Set up the spinners and recycler views
     */
    private fun setRecyclerViews(savedInstanceState: Bundle?) {
        home_github_spinner.visibility = View.VISIBLE
        home_meetup_spinner.visibility = View.VISIBLE
        if (savedInstanceState != null) {
            gitHubRecyclerViewLastPosition = savedInstanceState.getInt(Constants.KEY_GITHUB_RECYCLERVIEW_POSITION)
            gitHubRecyclerViewOption = savedInstanceState.getInt(Constants.KEY_GITHUB_RECYCLERVIEW_OPTION)
            meetupRecyclerViewLastPosition = savedInstanceState.getInt(Constants.KEY_MEETUP_RECYCLERVIEW_POSITION)
        }
        if (resources.configuration.smallestScreenWidthDp <= 600 && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // If the user is using a mobile and the orientation is portrait mode, then the
            // GitHubRecyclerView will use a LinearLayout scrolling horizontally (one item will
            // take the whole width)
            home_github_recycler_view.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false)
            gitHubRecyclerViewOption = 1
        } else if (resources.configuration.smallestScreenWidthDp <= 600 && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the user is using a mobile and the orientation is landscape mode, then the
            // GitHubRecyclerView will use a LinearLayout that will scroll vertically.
            home_github_recycler_view.layoutManager = LinearLayoutManager(this)
            gitHubRecyclerViewOption = 1
        } else {
            // Else (when using a tablet), a GridLayout will be used to display 3 items per line
            home_github_recycler_view.layoutManager = GridLayoutManager(this, 3)
            gitHubRecyclerViewOption = 2
        }
        home_meetup_recycler_view.layoutManager = GridLayoutManager(this, 2)
    }

    /**
     * Get the device current location (city accuracy)
     */
    private fun getLocation() {
        val locationRequest = LocationRequest().setInterval(Constants.LOCATION_UPDATE_INTERVAL).setPriority(LocationRequest.PRIORITY_LOW_POWER)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location: Location? ->
            if (location != null) {
                val currentLongitude: Double = location.longitude
                val currentLatitude: Double = location.latitude
                val gcd = Geocoder(applicationContext, Locale.getDefault())
                try {
                    // Request city level accuracy
                    val address: List<Address> = gcd.getFromLocation(currentLatitude, currentLongitude
                            , 1)
                    userLocation = address[0].locality

                    // Update the user location in the preferences
                    sharedPref!!.edit().putString(getString(R.string.pref_meetup_location_key),
                            userLocation).apply()
                    retrieveMeetupGroups()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                // Could not retrieve the location
                // Default location will be used
                retrieveMeetupGroups()
            }
        }
    }

    /**
     * Check if the location permission was granted in order to display trending Meetup tech
     * groups near the user
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(ACCESS_COARSE_LOCATION),
                    Constants.PERMISSIONS_REQUEST_COARSE_LOCATION)
        } else {
            // Permission is already granted
            getLocation()
        }
    }

    /**
     * Get the location permission result
     */
    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSIONS_REQUEST_COARSE_LOCATION) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == ACCESS_COARSE_LOCATION) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        // Location permission was granted this time
                        // Update the location switch in the preferences
                        sharedPref!!.edit().putBoolean(getString(R.string.pref_location_key), true).apply()
                        getLocation()
                    } else {
                        // Permission was not granted
                        // Use default location (Tokyo)
                        Toast.makeText(this, R.string.message_default_location,
                                Toast.LENGTH_LONG).show()
                        retrieveMeetupGroups()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_pref -> {
                val preferenceIntent = Intent(this, PreferenceActivity::class.java)
                startActivity(preferenceIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            menuItem.isChecked = true
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> {
            }
            R.id.nav_bookmarks -> {
                val bookmarkIntent = Intent(this, BookmarksActivity::class.java)
                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                startActivity(bookmarkIntent, bundle)
            }
            R.id.nav_github -> {
                val gitHubIntent = Intent(this@MainActivity, MainViewActivity::class.java)
                gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY)

                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                startActivity(gitHubIntent, bundle)
            }
            R.id.nav_meetup -> {
                val meetupIntent = Intent(this@MainActivity, MainViewActivity::class.java)
                meetupIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY)

                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                startActivity(meetupIntent, bundle)
            }
            else -> {
            }
        }
        drawer_layout.closeDrawers()
    }
}