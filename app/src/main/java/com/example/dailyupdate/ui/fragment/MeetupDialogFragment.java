package com.example.dailyupdate.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.dailyupdate.R;

public class MeetupDialogFragment extends DialogFragment {

    private EditText keywordInputEditText;
    private RadioButton sortByBest;
    private RadioButton sortByTime;
    private EditText locationInputEditText;

    private String searchKeyword;
    private String sortBy;
    private String searchLocation;
    private SharedPreferences sharedPref;
    private MeetupDialogListener listener;

    public interface MeetupDialogListener {
        void onMeetupDialogPositiveClick(DialogFragment dialog);

        void onMeetupDialogNegativeClick(DialogFragment dialog);
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
                    "NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meetup_dialog, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.meetup_dialog);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        builder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getDialogValues();
                listener.onMeetupDialogPositiveClick(MeetupDialogFragment.this);
            }
        }).setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onMeetupDialogNegativeClick(MeetupDialogFragment.this);
                // TODO: handle dialog cancel option
            }
        });
        return builder.create();
    }

    private void getDialogValues() {
        Dialog dialog = MeetupDialogFragment.this.getDialog();
        // Get the search keywords
        keywordInputEditText = dialog.findViewById(R.id.meetup_dialog_edittext_keywords_input);
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        searchKeyword = keywordInputValue;
        sharedPref.edit().putString(getString(R.string.pref_meetup_edittext_key), searchKeyword).apply();

        // Get the sorting preference. Default sorting option is by time order.
        sortByBest = dialog.findViewById(R.id.meetup_dialog_sortby_best);
        sortByTime = dialog.findViewById(R.id.meetup_dialog_sortby_time);
        if (sortByBest.isChecked()) {
            sortBy = getString(R.string.pref_meetup_sort_best_value);
        } else if (sortByTime.isChecked()) {
            sortBy = getString(R.string.pref_meetup_sort_time_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_meetup_sort_key), sortBy).apply();

        // Get the location
        locationInputEditText = dialog.findViewById(R.id.meetup_dialog_edittext_location_input);
        String locationInputValue = locationInputEditText.getText().toString();
        locationInputEditText.setText(locationInputValue);
        if (!locationInputValue.isEmpty()) {
            searchLocation = locationInputValue;
            sharedPref.edit().putString(getString(R.string.pref_meetup_location_key),
                    searchLocation).apply();
        }
    }
}
