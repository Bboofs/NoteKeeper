package com.loogs.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class NoteInfo implements Parcelable{
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    private NoteInfo(Parcel source){
       mCourse = source.readParcelable(CourseInfo.class.getClassLoader());
       mTitle = source.readString();
       mText = source.readString();
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mCourse, 0);
        parcel.writeString(mTitle);
        parcel.writeString(mText);
    }

    public final static Parcelable.Creator<NoteInfo> CREATOR;

    static {
        CREATOR = new Creator<NoteInfo>() {

            @Override
            public NoteInfo createFromParcel(Parcel parcel) {
                return new NoteInfo(parcel);
            }

            @Override
            public NoteInfo[] newArray(int i) {
                return new NoteInfo[i];
            }
        };
    }
}

/*
    // method one of calling anonymous class
    public final static Parcelable.Creator<NoteInfo> CREATOR;

    static {
        CREATOR = new Creator<NoteInfo>() {

            @Override
            public NoteInfo createFromParcel(Parcel parcel) {
                return new NoteInfo(parcel);
            }

            @Override
            public NoteInfo[] newArray(int i) {
                return new NoteInfo[i];
            }
        };
    }

       // method one of calling anonymous class
       public final static Parcelable.Creator<NoteInfo> CREATOR = new Parcelable.Creator<NoteInfo>() {

            @Override
            public NoteInfo createFromParcel(Parcel parcel) {
                return new NoteInfo(parcel);
            }

            @Override
            public NoteInfo[] newArray(int i) {
                return new NoteInfo[i];
            }
        };

* */