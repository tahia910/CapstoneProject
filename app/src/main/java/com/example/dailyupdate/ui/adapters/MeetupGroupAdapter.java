package com.example.dailyupdate.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupGroupAdapter extends RecyclerView.Adapter<MeetupGroupAdapter.MeetupGroupAdapterViewHolder> {

    @BindView(R.id.textview_group_title) TextView groupTitleTextView;
    @BindView(R.id.textview_group_members) TextView groupMembersTextView;
    @BindView(R.id.imageview_group_image) ImageView groupImageView;

    private Context context;
    private static ClickListener clickListener;
    private final List<MeetupGroup> meetupGroupList;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MeetupGroupAdapter.clickListener = clickListener;
    }

    public MeetupGroupAdapter(Context context, List<MeetupGroup> meetupGroupList) {
        this.context = context;
        this.meetupGroupList = meetupGroupList;
    }

    @Override
    public MeetupGroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.home_meetupgroup_item, parent, false);
        ButterKnife.bind(this, view);
        return new MeetupGroupAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetupGroupAdapterViewHolder viewHolder, int i) {
        MeetupGroup meetupGroup = meetupGroupList.get(i);
        String groupMembersString = meetupGroup.getGroupMembers() + context.getString(R.string.meetupgroup_members_label);

        if (meetupGroup.getGroupPhoto() != null) {
            String groupImageUrl = meetupGroup.getGroupPhoto().getGroupPhotoUrl();
            Glide.with(groupImageView.getContext()).load(groupImageUrl).centerCrop().into(groupImageView);
        }
        groupTitleTextView.setText(meetupGroup.getGroupName());
        groupMembersTextView.setText(groupMembersString);
    }

    @Override
    public int getItemCount() {
        if (null == meetupGroupList) return 0;
        return meetupGroupList.size();
    }

    public class MeetupGroupAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MeetupGroupAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
