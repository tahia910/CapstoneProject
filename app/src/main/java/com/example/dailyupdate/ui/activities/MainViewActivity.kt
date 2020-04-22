package com.example.dailyupdate.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.dailyupdate.R
import com.example.dailyupdate.ui.fragments.GitHubMainFragment
import com.example.dailyupdate.ui.fragments.dialogs.GitHubDialogFragment
import com.example.dailyupdate.ui.fragments.dialogs.GitHubDialogFragment.GitHubDialogListener
import com.example.dailyupdate.utilities.Constants
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_appbar_layout.*
import kotlinx.android.synthetic.main.main_drawer.*

class MainViewActivity :
        AppCompatActivity(),
        GitHubDialogListener{

    private var mainViewOption: String? = null
    private var sharedPref: SharedPreferences? = null
    private var sharedPrefMeetupSearchKeyword: String? = null
    private var sharedPrefGitHubSearchKeyword: String? = null
    private var gitHubDialogLatestSearchKeyword: String? = null
    private var gitHubDialogLatestSortBy: String? = null
    private var gitHubDialogLatestSearchOrder: String? = null
    private var meetupDialogLatestSearchKeyword: String? = null
    private var meetupDialogLatestSortBy: String? = null
    private var meetupDialogLatestLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_drawer)
        setActionBar()
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val bundle = intent.extras
        if (bundle != null) mainViewOption = bundle.getString(Constants.MAIN_KEY)

        if (savedInstanceState != null) {
            if (mainViewOption == Constants.MEETUP_MAIN_KEY) {
                meetupDialogLatestSearchKeyword = savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD)
                meetupDialogLatestSortBy = savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_SORT)
                meetupDialogLatestLocation = savedInstanceState.getString(Constants.KEY_MEETUP_DIALOG_LOCATION)
            } else if (mainViewOption == Constants.GITHUB_MAIN_KEY) {
                gitHubDialogLatestSearchKeyword = savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD)
                gitHubDialogLatestSortBy = savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_SORT)
                gitHubDialogLatestSearchOrder = savedInstanceState.getString(Constants.KEY_GITHUB_DIALOG_ORDER)
            }
        }
        openDialogOrFragment(savedInstanceState)
    }

    /**
     * Decide which fragment or dialog to display from this activity.
     */
    private fun openDialogOrFragment(savedInstanceState: Bundle?) {
//        if (mainViewOption == Constants.MEETUP_MAIN_KEY) {
//            supportActionBar?.title = applicationContext.getString(R.string.meetup_search_title)
//            ViewModelProvider(this@MainViewActivity).get(BookmarksDatabaseViewModel::class.java)
//            sharedPrefMeetupSearchKeyword = sharedPref!!.getString(getString(R.string.pref_meetup_edittext_key), "")
//            // If the search keyword is empty, display the search dialog
//            if (sharedPrefMeetupSearchKeyword!!.isNotEmpty()) {
//                // If the savedInstanceState is not null, the previous fragment will restore
//                // itself automatically, so do not add another one on top
//                if (savedInstanceState == null) {
//                    supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                            .replace(R.id.fragment_container, MeetupMainFragment.newInstance()).commit()
//                }
//            } else {
//                getSearchDialog()
//            }
//        } else
            if (mainViewOption == Constants.GITHUB_MAIN_KEY) {
            supportActionBar?.title = applicationContext.getString(R.string.github_search_title)
            sharedPrefGitHubSearchKeyword = sharedPref!!.getString(getString(R.string.pref_github_edittext_key), "")
            if (sharedPrefGitHubSearchKeyword!!.isNotEmpty()) {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragment_container, GitHubMainFragment.newInstance()).commit()
                }
            } else {
                getSearchDialog()
            }
        }
    }

    /**
     * Set up the toolbar and drawer.
     */
    private fun setActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
        if (nav_view != null) setupDrawerContent(nav_view)
    }

    private fun setEmptyView() {
        appbar_layout_emptyview.visibility = View.VISIBLE
        appbar_layout_emptyview.text = getString(R.string.no_search_set_emptyview_message)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // If there is any data from a dialog's onSaveInstanceState(), save them here (in the
        // parent activity) to retrieve them when the activity is restored and launches the
        // dialog again
        if (gitHubDialogLatestSearchKeyword != null || gitHubDialogLatestSortBy != null || gitHubDialogLatestSearchOrder != null) {
            outState.putString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD, gitHubDialogLatestSearchKeyword)
            outState.putString(Constants.KEY_GITHUB_DIALOG_SORT, gitHubDialogLatestSortBy)
            outState.putString(Constants.KEY_GITHUB_DIALOG_ORDER, gitHubDialogLatestSearchOrder)
        }
        if (meetupDialogLatestSearchKeyword != null || meetupDialogLatestSortBy != null || meetupDialogLatestLocation != null) {
            outState.putString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD, meetupDialogLatestSearchKeyword)
            outState.putString(Constants.KEY_MEETUP_DIALOG_SORT, meetupDialogLatestSortBy)
            outState.putString(Constants.KEY_MEETUP_DIALOG_LOCATION, meetupDialogLatestLocation)
        }
    }

    /**
     * Get the search dialog for either Meetup events or GitHub repositories, set as arguments
     * the values previously inputted and saved in the dialog's onSaveInstanceState().
     * (The null check for each values will be done before setting them back in the dialog's
     * setSavedInstanceStateValues() method.)
     */
    private fun getSearchDialog() {
        if (mainViewOption == Constants.MEETUP_MAIN_KEY) {
            val args = Bundle().apply {
                putString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD, meetupDialogLatestSearchKeyword)
                putString(Constants.KEY_MEETUP_DIALOG_SORT, meetupDialogLatestSortBy)
                putString(Constants.KEY_MEETUP_DIALOG_LOCATION, meetupDialogLatestLocation)
            }

//            MeetupDialogFragment().apply {
//                arguments = args
//                enterTransition = R.anim.fade_in
//                exitTransition = R.anim.fade_out
//            }.show(supportFragmentManager, "meetup_search")

        } else if (mainViewOption == Constants.GITHUB_MAIN_KEY) {
            val args = Bundle().apply {
                putString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD, gitHubDialogLatestSearchKeyword)
                putString(Constants.KEY_GITHUB_DIALOG_SORT, gitHubDialogLatestSortBy)
                putString(Constants.KEY_GITHUB_DIALOG_ORDER, gitHubDialogLatestSearchOrder)
            }

            GitHubDialogFragment().apply {
                arguments = args
                enterTransition = R.anim.fade_in
                exitTransition = R.anim.fade_out
            }.show(supportFragmentManager, "github_search")
        }
    }

    /**
     * Callback from MeetupDialogFragment (Meetup events search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button.
     */
//    override fun onMeetupDialogPositiveClick(dialog: DialogFragment) {
//        sharedPrefMeetupSearchKeyword = sharedPref!!.getString(getString(R.string.pref_meetup_edittext_key), "")
//        if (sharedPrefMeetupSearchKeyword!!.isNotEmpty()) {
//            supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                    .replace(R.id.fragment_container, MeetupMainFragment.newInstance()).commit()
//        } else {
//            setEmptyView()
//            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
//                    Toast.LENGTH_LONG).show()
//        }
//    }

//    override fun onMeetupDialogNegativeClick(dialog: DialogFragment) {
//        setEmptyView()
//        Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
//                Toast.LENGTH_LONG).show()
//    }

    /**
     * Callback from the Meetup event search dialog, sending the user latest input before
     * the dialog was destroyed.
     */
//    override fun restoreMeetupDialogState(bundle: Bundle) {
//        meetupDialogLatestSearchKeyword = bundle.getString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD)
//        meetupDialogLatestSortBy = bundle.getString(Constants.KEY_MEETUP_DIALOG_SORT)
//        meetupDialogLatestLocation = bundle.getString(Constants.KEY_MEETUP_DIALOG_LOCATION)
//    }

    /**
     * Callback from GitHubDialogFragment (GitHub search dialog) to display the result of the
     * inputted search after the user clicked on the "Search" button.
     */
    override fun onGitHubDialogPositiveClick(dialog: DialogFragment) {
        sharedPrefGitHubSearchKeyword = sharedPref!!.getString(getString(R.string.pref_github_edittext_key), "")
        if (sharedPrefGitHubSearchKeyword!!.isNotEmpty()) {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, GitHubMainFragment.newInstance()).commit()
        } else {
            setEmptyView()
            Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onGitHubDialogNegativeClick(dialog: DialogFragment) {
        setEmptyView()
        Toast.makeText(this, getString(R.string.mainview_toast_empty_search),
                Toast.LENGTH_LONG).show()
    }

    /**
     * Callback from the GitHub repository search dialog, sending the user latest input before
     * the dialog was destroyed.
     */
    override fun restoreGitHubDialogState(bundle: Bundle) {
        gitHubDialogLatestSearchKeyword = bundle.getString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD)
        gitHubDialogLatestSortBy = bundle.getString(Constants.KEY_GITHUB_DIALOG_SORT)
        gitHubDialogLatestSearchOrder = bundle.getString(Constants.KEY_GITHUB_DIALOG_ORDER)
    }

    /**
     * Callback from MeetupMainFragment (fragment displaying the result of a Meetup event search)
     * to open the details of a selected event in a dialog.
     * Check if the fragment is already displayed just in case.
     */
//    override fun currentEventInfo(groupUrl: String, eventId: String) {
//        val originalFragment =
//                supportFragmentManager.findFragmentByTag(Constants.TAG_EVENT_DETAILS_FRAGMENT) as MeetupDetailsFragment?
//        originalFragment?.dismiss()
//
//        MeetupDetailsFragment.newInstance(groupUrl, eventId).apply {
//            setHasOptionsMenu(true)
//            setMenuVisibility(true)
//            setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen)
//        }.show(supportFragmentManager, Constants.TAG_EVENT_DETAILS_FRAGMENT)
//    }

    /**
     * MeetupDetailsFragment callback used for the BookmarksActivity, where there is a
     * possibility that the fragment gets displayed twice due to the widget.
     */
//    override fun closedFragmentCallback() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_search -> {
                appbar_layout_emptyview.visibility = View.GONE
                getSearchDialog()
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
        ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.content_description_open_drawer,
                R.string.content_description_close_drawer)
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            menuItem.isChecked = true
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> NavUtils.navigateUpFromSameTask(this)
            R.id.nav_bookmarks -> {
//                val bookmarkIntent = Intent(this, BookmarksActivity::class.java)
//                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
//                startActivity(bookmarkIntent, bundle)
            }
            R.id.nav_github -> if (mainViewOption != Constants.GITHUB_MAIN_KEY) {
                val gitHubIntent = Intent(this, MainViewActivity::class.java)
                gitHubIntent.putExtra(Constants.MAIN_KEY, Constants.GITHUB_MAIN_KEY)
                val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                startActivity(gitHubIntent, bundle)
            }
            else -> {
            }
        }
        drawer_layout.closeDrawers()
    }
}