package com.example.dailyupdate.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class GitHubDialogFragment extends DialogFragment {

    private String searchKeyword;
    private String sortBy;
    private String searchOrder;
    SharedPreferences sharedPref;

    public interface GitHubDialogListener {
        void onGitHubDialogPositiveClick(DialogFragment dialog);

        void onGitHubDialogNegativeClick(DialogFragment dialog);
    }

    GitHubDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (GitHubDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement " +
                    "NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.github_dialog, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.github_dialog);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        builder.setPositiveButton(R.string.save_label, (dialog, id) -> {
            getDialogValues();
            listener.onGitHubDialogPositiveClick(GitHubDialogFragment.this);
        }).setNegativeButton(R.string.cancel_label, (dialog, id) -> {
            listener.onGitHubDialogNegativeClick(GitHubDialogFragment.this);
            // TODO: handle dialog cancel option
        });
        return builder.create();
    }

    private void getDialogValues() {
        Dialog dialog = GitHubDialogFragment.this.getDialog();

        // Get the search keywords
        EditText keywordInputEditText =
                (EditText) dialog.findViewById(R.id.github_dialog_edittext_keywords_input);
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        searchKeyword = keywordInputValue;
        sharedPref.edit().putString(getString(R.string.pref_github_edittext_key), searchKeyword).apply();

        // Get the sorting preference. Default sorting option is by updated date.
        RadioButton sortByUpdated =
                (RadioButton) dialog.findViewById(R.id.github_dialog_sortby_updated);
        RadioButton sortByStars =
                (RadioButton) dialog.findViewById(R.id.github_dialog_sortby_stars);
        RadioButton sortByForks =
                (RadioButton) dialog.findViewById(R.id.github_dialog_sortby_forks);
        if (sortByUpdated.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_updated_value);
        } else if (sortByStars.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_stars_value);
        } else if (sortByForks.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_forks_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_github_sort_key), sortBy).apply();

        // Get the order preference. Default order option is descending.
        RadioButton orderDescending =
                (RadioButton) dialog.findViewById(R.id.github_dialog_order_descending_option);
        RadioButton orderAscending =
                (RadioButton) dialog.findViewById(R.id.github_dialog_order_ascending_option);
        if (orderDescending.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_descending_value);
        } else if (orderAscending.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_ascending_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_github_order_key), searchOrder).apply();
    }

}

