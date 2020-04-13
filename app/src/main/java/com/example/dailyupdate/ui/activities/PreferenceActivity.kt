package com.example.dailyupdate.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyupdate.R
import com.example.dailyupdate.ui.fragments.PreferenceFragment
import kotlinx.android.synthetic.main.main_appbar_layout.*

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_appbar_layout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                PreferenceFragment()).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
