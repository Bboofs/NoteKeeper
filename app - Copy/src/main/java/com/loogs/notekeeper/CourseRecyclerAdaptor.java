package com.loogs.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseRecyclerAdaptor extends RecyclerView.Adapter <CourseRecyclerAdaptor.ViewHolder> {

    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdaptor(Context mContext, List<CourseInfo> courses) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mCourses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // before creating view holder, create the view itself
        // local var itemView uses the LayoutInflator which has a method called inflate
        // first param to inflate is the layout resource you want to inflate
        // next is the view group that is being inflated within (the parent param passed as arg to this method)
        // false = the newly created group should not automatically attache to it's parent
        // i.e. the itemView points to the root
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseInfo course = mCourses.get(position);
        holder.mTextCourse.setText(course.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public int mCurrentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, mCourses.get(mCurrentPosition).getTitle(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
