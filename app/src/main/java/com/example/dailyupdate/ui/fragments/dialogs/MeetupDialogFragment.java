package com.example.dailyupdate.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.utilities.Constants;

public class MeetupDialogFragment extends DialogFragment {

    private EditText keywordInputEditText;
    private RadioButton sortByBestOption;
    private RadioButton sortByTimeOption;
    private EditText locationInputEditText;

    private String searchKeyword;
    private String sortBy;
    private String searchLocation;
    private SharedPreferences sharedPref;
    private MeetupDialogListener listener;

    public interface MeetupDialogListener {
        void onMeetupDialogPositiveClick(DialogFragment dialog);

        void onMeetupDialogNegativeClick(DialogFragment dialog);

        void restoreMeetupDialogState(Bundle bundle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MeetupDialogListener so we can send events to the host
            listener = (MeetupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement " +
                    "MeetupDialogListener");
        }
    }

    /**
     * Wait until the dialog view is ready before setting back the previously inputted values
     **/
    @Override
    public void onResume() {
        super.onResume();
        setSavedInstanceStateValues(searchKeyword, sortBy, searchLocation);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.meetup_dialog);
        }
        if (getArguments() != null) {
            searchKeyword = getArguments().getString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD);
            sortBy = getArguments().getString(Constants.KEY_MEETUP_DIALOG_SORT);
            searchLocation = getArguments().getString(Constants.KEY_MEETUP_DIALOG_LOCATION);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        builder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bindViews();
                getDialogValues();
                listener.onMeetupDialogPositiveClick(MeetupDialogFragment.this);
            }
        }).setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onMeetupDialogNegativeClick(MeetupDialogFragment.this);
            }
        });
        return builder.create();
    }

    private void bindViews() {
        Dialog dialog = MeetupDialogFragment.this.getDialog();
        keywordInputEditText = dialog.findViewById(R.id.meetup_dialog_edittext_keywords_input);
        sortByBestOption = dialog.findViewById(R.id.meetup_dialog_sortby_best);
        sortByTimeOption = dialog.findViewById(R.id.meetup_dialog_sortby_time);
        locationInputEditText = dialog.findViewById(R.id.meetup_dialog_edittext_location_input);
    }

    /**
     * Retrieve the search values and update the SharedPreferences
     **/
    private void getDialogValues() {
        // Get the search keywords
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        searchKeyword = keywordInputValue;
        sharedPref.edit().putString(getString(R.string.pref_meetup_edittext_key), searchKeyword).apply();

        // Get the sorting preference. Default sorting option is by time order.
        if (sortByBestOption.isChecked()) {
            sortBy = getString(R.string.pref_meetup_sort_best_value);
        } else if (sortByTimeOption.isChecked()) {
            sortBy = getString(R.string.pref_meetup_sort_time_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_meetup_sort_key), sortBy).apply();

        // Get the location
        String locationInputValue = locationInputEditText.getText().toString();
        locationInputEditText.setText(locationInputValue);
        if (!locationInputValue.isEmpty()) {
            searchLocation = locationInputValue;
            sharedPref.edit().putString(getString(R.string.pref_meetup_location_key),
                    searchLocation).apply();
        }
    }

    /**
     * Set back the previously inputted values unless they are empty
     **/
    private void setSavedInstanceStateValues(String searchKeyword, String sortBy,
                                             String searchLocation) {
        bindViews();
        if (searchKeyword != null) {
            keywordInputEditText.setText(searchKeyword);
        }
        if (sortBy != null) {
            if (sortBy.equals(getString(R.string.pref_meetup_sort_best_value))) {
                sortByBestOption.setChecked(true);
            } else if (sortBy.equals(getString(R.string.pref_meetup_sort_time_value))) {
                sortByTimeOption.setChecked(true);
            }
        }
        if (searchLocation != null) {
            locationInputEditText.setText(searchLocation);
        }
    }

    /**
     * Save the search value input by user in case of screen rotation
     **/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        bindViews();
        String keywordInputValue = keywordInputEditText.getText().toString();
        outState.putString(Constants.KEY_MEETUP_DIALOG_SEARCH_KEYWORD, keywordInputValue);

        String latestSortByValue = getString(R.string.pref_meetup_sort_time_value);
        if (sortByBestOption.isChecked()) {
            latestSortByValue = getString(R.string.pref_meetup_sort_best_value);
        } else if (sortByTimeOption.isChecked()) {
            latestSortByValue = getString(R.string.pref_meetup_sort_time_value);
        }
        outState.putString(Constants.KEY_MEETUP_DIALOG_SORT, latestSortByValue);

        String latestLocationValue = locationInputEditText.getText().toString();
        outState.putString(Constants.KEY_MEETUP_DIALOG_LOCATION, latestLocationValue);

        // Put the values in a listener linked to the parent activity,
        // which will also put the values in its onSaveInstanceState().
        listener.restoreMeetupDialogState(outState);
    }

}
