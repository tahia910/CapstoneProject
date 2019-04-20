package com.example.dailyupdate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.MeetupGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MeetupGroupAdapter extends RecyclerView.Adapter<MeetupGroupAdapter.MeetupGroupAdapterViewHolder> {

    private Context context;
    private final List<MeetupGroup> meetupGroupList;
    private int layoutOption;
    int layoutIdForListItem;

    public MeetupGroupAdapter(Context context, List<MeetupGroup> meetupGroupList,
                              int layoutOption) {
        this.context = context;
        this.meetupGroupList = meetupGroupList;
        this.layoutOption = layoutOption;
    }

    @Override
    public MeetupGroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        if(layoutOption == 1){
            layoutIdForListItem = R.layout.home_meetupgroup_item;
        } else if (layoutOption == 2){
            layoutIdForListItem = R.layout.meetup_main_group_item;
        }
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MeetupGroupAdapterViewHolder viewHolder = new MeetupGroupAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeetupGroupAdapterViewHolder viewHolder, int i) {
        MeetupGroup meetupGroup = meetupGroupList.get(i);
        String groupTitle = meetupGroup.getGroupName();
        int groupMembers = meetupGroup.getGroupMembers();
        String groupMembersString =
                String.valueOf(groupMembers) + context.getString(R.string.meetupgroup_members_label);

        if (meetupGroup.getGroupPhoto() != null) {
            String groupImageUrl = meetupGroup.getGroupPhoto().getGroupPhotoUrl();
            Glide.with(viewHolder.groupImageView.getContext()).load(groupImageUrl).centerCrop().into(viewHolder.groupImageView);
        }
        // TODO: handle empty image case

        viewHolder.groupTitleTextView.setText(groupTitle);
        viewHolder.groupMembersTextView.setText(groupMembersString);
    }

    @Override
    public int getItemCount() {
        if (null == meetupGroupList) return 0;
        return meetupGroupList.size();
    }

    public class MeetupGroupAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView groupTitleTextView;
        private final TextView groupMembersTextView;
        private final ImageView groupImageView;

        private MeetupGroupAdapterViewHolder(View view) {
            super(view);
            groupTitleTextView = (TextView) view.findViewById(R.id.textview_group_title);
            groupMembersTextView = (TextView) view.findViewById(R.id.textview_group_members);
            groupImageView = (ImageView) view.findViewById(R.id.imageview_group_image);
        }
    }
}
