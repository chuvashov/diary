package com.example.diary.profile;

public class Profile {

    /**
     * Item name
     */
    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    /**
     * Item surname
     */
    @com.google.gson.annotations.SerializedName("surname")
    private String mSurname;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item email
     */
    @com.google.gson.annotations.SerializedName("email")
    private String mEmail;

    /**
     * Item phone
     */
    @com.google.gson.annotations.SerializedName("phone")
    private String mPhone;

    /**
     * Item info
     */
    @com.google.gson.annotations.SerializedName("info")
    private String mInfo;

    /**
     * Profile constructor
     */
    public Profile() {

    }

    /**
     * Returns the item name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the item name
     *
     * @param name
     *            name to set
     */
    public final void setName(String name) {
        mName = name;
    }

    /**
     * Returns the item surname
     */
    public String getSurname() {
        return mSurname;
    }

    /**
     * Sets the item surname
     *
     * @param surname
     *            surname to set
     */
    public final void setSurname(String surname) {
        mSurname = surname;
    }

    /**
     * Returns the item email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Sets the item email
     *
     * @param email
     *            email to set
     */
    public final void setEmail(String email) {
        mEmail = email;
    }

    /**
     * Returns the item phone
     */
    public String getPhone() {
        return mPhone;
    }

    /**
     * Sets the item phone
     *
     * @param phone
     *            phone to set
     */
    public final void setPhone(String phone) {
        mPhone = phone;
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
     * Returns the item info
     */
    public String getInfo() {
        return mInfo;
    }

    /**
     * Sets the item info
     *
     * @param info
     *            info to set
     */
    public final void setInfo(String info) {
        mInfo = info;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Profile && ((Profile) o).mId == mId;
    }
}
