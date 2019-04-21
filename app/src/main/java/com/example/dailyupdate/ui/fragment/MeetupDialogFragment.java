package com.example.dailyupdate.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.DialogFragment;

import com.example.dailyupdate.R;

public class MeetupDialogFragment extends DialogFragment {


    public static final String KEY_KEYWORD = "key_keyword";
    public static final String KEY_SORT_BY = "key_sort_by";
    public static final String KEY_LOCATION = "key_location";
    private String searchKeyword;
    private String sortBy;
    private String searchLocation;

    public interface MeetupDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, Bundle bundle);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    MeetupDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
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
        builder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO: handle empty keyword/location
                getDialogValues();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_KEYWORD, searchKeyword);
                bundle.putString(KEY_SORT_BY, sortBy);
                bundle.putString(KEY_LOCATION, searchLocation);
                listener.onDialogPositiveClick(MeetupDialogFragment.this, bundle);
            }
        }).setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onDialogNegativeClick(MeetupDialogFragment.this);
                // TODO: handle dialog cancel option
                // reset options? (EditText.setText(null);)
            }
        });
        return builder.create();
    }

    private void getDialogValues() {
        Dialog dialog = MeetupDialogFragment.this.getDialog();

        // Get the search keywords
        EditText keywordInputEditText =
                (EditText) dialog.findViewById(R.id.edittext_keywords_input);
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        if (!keywordInputValue.isEmpty()) {
            searchKeyword = keywordInputValue;
        }

        // Get the sorting preference. Default sorting option is by Meetup groups.
        RadioButton sortByGroups = (RadioButton) dialog.findViewById(R.id.sortby_group);
        RadioButton sortByCalendar = (RadioButton) dialog.findViewById(R.id.sortby_calendar);
        if (sortByGroups.isChecked()) {
            sortBy = "groups";
        } else if (sortByCalendar.isChecked()) {
            sortBy = "events";
        }

        // Get the location
        // TODO: put current user location if known
        EditText locationInputEditText =
                (EditText) dialog.findViewById(R.id.edittext_location_input);
        String locationInputValue = locationInputEditText.getText().toString();
        locationInputEditText.setText(locationInputValue);
        if (!locationInputValue.isEmpty()) {
            searchLocation = locationInputValue;
        }
    }

}
