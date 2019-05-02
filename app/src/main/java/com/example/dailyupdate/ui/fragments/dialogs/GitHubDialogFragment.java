package com.example.dailyupdate.ui.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class GitHubDialogFragment extends DialogFragment {

    private String searchKeyword;
    private String sortBy;
    private String searchOrder;
    private SharedPreferences sharedPref;
    private GitHubDialogListener listener;
    private EditText keywordInputEditText;
    private RadioButton sortByUpdatedOption;
    private RadioButton sortByStarsOption;
    private RadioButton sortByForksOption;
    private RadioButton orderDescendingOption;
    private RadioButton orderAscendingOption;

    public interface GitHubDialogListener {
        void onGitHubDialogPositiveClick(DialogFragment dialog);

        void onGitHubDialogNegativeClick(DialogFragment dialog);

        void restoreGitHubDialogState(Bundle bundle);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GitHubDialogListener so we can send events to the host
            listener = (GitHubDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement " +
                    "GitHubDialogListener");
        }
    }

    /**
     * Wait until the dialog view is ready before setting back the previously inputted values
     **/
    @Override
    public void onResume() {
        super.onResume();
        setSavedInstanceStateValues(searchKeyword, sortBy, searchOrder);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.github_dialog);
        }
        if (getArguments() != null) {
            searchKeyword = getArguments().getString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD);
            sortBy = getArguments().getString(Constants.KEY_GITHUB_DIALOG_SORT);
            searchOrder = getArguments().getString(Constants.KEY_GITHUB_DIALOG_ORDER);
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        builder.setPositiveButton(R.string.save_label, (dialog, id) -> {
            bindViews();
            getDialogValues();
            listener.onGitHubDialogPositiveClick(GitHubDialogFragment.this);
        }).setNegativeButton(R.string.cancel_label, (dialog, id) -> {
            listener.onGitHubDialogNegativeClick(GitHubDialogFragment.this);
        });
        return builder.create();
    }

    private void bindViews() {
        Dialog dialog = GitHubDialogFragment.this.getDialog();
        keywordInputEditText = dialog.findViewById(R.id.github_dialog_edittext_keywords_input);
        sortByUpdatedOption = dialog.findViewById(R.id.github_dialog_sortby_updated);
        sortByStarsOption = dialog.findViewById(R.id.github_dialog_sortby_stars);
        sortByForksOption = dialog.findViewById(R.id.github_dialog_sortby_forks);
        orderDescendingOption = dialog.findViewById(R.id.github_dialog_order_descending_option);
        orderAscendingOption = dialog.findViewById(R.id.github_dialog_order_ascending_option);
    }

    /**
     * Retrieve the search values and update the SharedPreferences
     **/
    private void getDialogValues() {
        // Get the search keywords
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        searchKeyword = keywordInputValue;
        sharedPref.edit().putString(getString(R.string.pref_github_edittext_key), searchKeyword).apply();

        // Get the sorting preference. Default sorting option is by updated date.
        if (sortByUpdatedOption.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_updated_value);
        } else if (sortByStarsOption.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_stars_value);
        } else if (sortByForksOption.isChecked()) {
            sortBy = getString(R.string.pref_github_sort_forks_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_github_sort_key), sortBy).apply();

        // Get the order preference. Default order option is descending.
        if (orderDescendingOption.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_descending_value);
        } else if (orderAscendingOption.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_ascending_value);
        }
        sharedPref.edit().putString(getString(R.string.pref_github_order_key), searchOrder).apply();
    }

    /**
     * Set back the previously inputted values unless they are empty
     **/
    private void setSavedInstanceStateValues(String searchKeyword, String sortBy,
                                             String searchOrder) {
        bindViews();
        if (searchKeyword != null) {
            keywordInputEditText.setText(searchKeyword);
        }
        if (sortBy != null) {
            if (sortBy.equals(getString(R.string.pref_github_sort_updated_value))) {
                sortByUpdatedOption.setChecked(true);
            } else if (sortBy.equals(getString(R.string.pref_github_sort_stars_value))) {
                sortByStarsOption.setChecked(true);
            } else if (sortBy.equals(getString(R.string.pref_github_sort_forks_value))) {
                sortByForksOption.setChecked(true);
            }
        }
        if (searchOrder != null) {
            if (searchOrder.equals(getString(R.string.pref_github_order_descending_value))) {
                orderDescendingOption.setChecked(true);
            } else if (searchOrder.equals(getString(R.string.pref_github_order_ascending_value))) {
                orderAscendingOption.setChecked(true);
            }
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
        outState.putString(Constants.KEY_GITHUB_DIALOG_SEARCH_KEYWORD, keywordInputValue);

        String latestSortByValue = getString(R.string.pref_github_sort_updated_value);
        if (sortByUpdatedOption.isChecked()) {
            latestSortByValue = getString(R.string.pref_github_sort_updated_value);
        } else if (sortByStarsOption.isChecked()) {
            latestSortByValue = getString(R.string.pref_github_sort_stars_value);
        } else if (sortByForksOption.isChecked()) {
            latestSortByValue = getString(R.string.pref_github_sort_forks_value);
        }
        outState.putString(Constants.KEY_GITHUB_DIALOG_SORT, latestSortByValue);

        String latestSearchOrderValue = getString(R.string.pref_github_order_descending_value);
        if (orderDescendingOption.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_descending_value);
        } else if (orderAscendingOption.isChecked()) {
            searchOrder = getString(R.string.pref_github_order_ascending_value);
        }
        outState.putString(Constants.KEY_GITHUB_DIALOG_ORDER, latestSearchOrderValue);

        // Put the values in a listener linked to the parent activity,
        // which will also put the values in its onSaveInstanceState().
        listener.restoreGitHubDialogState(outState);
    }
}

