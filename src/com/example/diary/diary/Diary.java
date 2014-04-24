package com.example.diary.diary;

import java.util.Date;

/**
 * Represents an item in a DiaryFragment
 */
public class Diary {

    /**
     * User id
     */
    @com.google.gson.annotations.SerializedName("user_id")
    private String mUserId;

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String mText;

    /**
     * Item id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item title
     */
    @com.google.gson.annotations.SerializedName("title")
    private String mTitle;

    /**
     * Item date
     */
    @com.google.gson.annotations.SerializedName("date")
    private Date mDate;

    public Diary() {

    }

    /**
     * Returns the item title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the item title
     *
     * @param title
     *            title to set
     */
    public final void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setText(String text) {
        mText = text;
    }

    /**
     * Returns the item date
     */
    public Date getDate() {
        return mDate;
    }

    /**
     * Sets the item date
     *
     * @param date
     *            date to set
     */
    public final void setDate(Date date) {
        mDate = date;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }


    /**
     * Returns the items user id
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * Sets the item user id
     *
     * @param userId
     *            id to set
     */
    public final void setUserId(String userId) {
        mUserId = userId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Diary && ((Diary) o).mId == mId;
    }
}
