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

public class GitHubDialogFragment extends DialogFragment {

    public static final String KEY_GITHUB_KEYWORD = "key_keyword";
    public static final String KEY_GITHUB_SORT_BY = "key_sort_by";
    public static final String KEY_GITHUB_ORDER = "key_order";
    private String searchKeyword;
    private String sortBy;
    private String searchOrder;

    public interface GitHubDialogListener {
        void onGitHubDialogPositiveClick(DialogFragment dialog, Bundle bundle);

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
        builder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO: handle empty keyword
                getDialogValues();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_GITHUB_KEYWORD, searchKeyword);
                bundle.putString(KEY_GITHUB_SORT_BY, sortBy);
                bundle.putString(KEY_GITHUB_ORDER, searchOrder);
                listener.onGitHubDialogPositiveClick(GitHubDialogFragment.this, bundle);
            }
        }).setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onGitHubDialogNegativeClick(GitHubDialogFragment.this);
                // TODO: handle dialog cancel option
                // reset options? (EditText.setText(null);)
            }
        });
        return builder.create();
    }

    private void getDialogValues() {
        Dialog dialog = GitHubDialogFragment.this.getDialog();

        // Get the search keywords
        EditText keywordInputEditText =
                (EditText) dialog.findViewById(R.id.edittext_keywords_input);
        String keywordInputValue = keywordInputEditText.getText().toString();
        keywordInputEditText.setText(keywordInputValue);
        if (!keywordInputValue.isEmpty()) {
            searchKeyword = keywordInputValue;
        }

        // Get the sorting preference. Default sorting option is by updated date.
        RadioButton sortByUpdated = (RadioButton) dialog.findViewById(R.id.sortby_updated);
        RadioButton sortByStars = (RadioButton) dialog.findViewById(R.id.sortby_stars);
        RadioButton sortByForks = (RadioButton) dialog.findViewById(R.id.sortby_forks);
        if (sortByUpdated.isChecked()) {
            sortBy = "updated";
        } else if (sortByStars.isChecked()) {
            sortBy = "stars";
        } else if (sortByForks.isChecked()) {
            sortBy = "forks";
        }

        // Get the order preference. Default order option is descending.
        RadioButton orderDescending =
                (RadioButton) dialog.findViewById(R.id.order_descending_option);
        RadioButton orderAscending = (RadioButton) dialog.findViewById(R.id.order_ascending_option);
        if (orderDescending.isChecked()) {
            searchOrder = "descending";
        } else if (orderAscending.isChecked()) {
            searchOrder = "ascending";
        }
    }

}

