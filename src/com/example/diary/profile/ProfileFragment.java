package com.example.diary.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.diary.Dialog;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Profile> mProfileTable;

    /**
     * EditText used to editing name
     */
    private EditText editName;

    /**
     * EditText used to editing surname
     */
    private EditText editSurname;

    /**
     * EditText used to editing email
     */
    private EditText editEmail;

    /**
     * EditText used to editing phone
     */
    private EditText editPhone;

    /**
     * EditText used to editing info
     */
    private EditText editInfo;

    /**
     * Button used to editing profile information
     */
    private Button editButton;

    /**
     * Button used to confirmation changes
     */
    private Button okButton;

    /**
     * Button used to cancellation changes
     */
    private Button cancelButton;

    /**
     * String containing phone
     */
    private String phone;

    /**
     * String containing email
     */
    private String email;

    /**
     * String containing name
     */
    private String name;

    /**
     * String containing surname
     */
    private String surname;

    /**
     * String containing info
     */
    private String info;

    /**
     * Profile of current user
     */
    private Profile profile;

    /**
     * View containing positive and negative buttons used
     * for confirmation and cancellation changes
     */
    private View buttonsView;

    /**
     * List containing all EditTexts
     */
    private List<EditText> editTexts;

    /**
     * Creates view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_profile, null);

        // Initialize edit texts
        editName = (EditText) view.findViewById(R.id.editName);
        editSurname = (EditText) view.findViewById(R.id.editSurname);
        editEmail = (EditText) view.findViewById(R.id.editEmail);
        editPhone = (EditText) view.findViewById(R.id.editPhone);
        editInfo = (EditText) view.findViewById(R.id.editInfo);

        // Initialize array of edit texts
        editTexts = new ArrayList<EditText>();
        editTexts.add(editEmail);
        editTexts.add(editInfo);
        editTexts.add(editName);
        editTexts.add(editPhone);
        editTexts.add(editSurname);

        buttonsView = view.findViewById(R.id.okCancelButtonsLayout);
        buttonsView.setVisibility(View.INVISIBLE);

        setActiveEditTexts(false);

        // Create listener to edit button
        editButton = (Button) view.findViewById(R.id.editProfileButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(true);
                saveProfileInfo();
                editButton.setVisibility(View.INVISIBLE);
                buttonsView.setVisibility(View.VISIBLE);
            }
        });

        // Create listener to ok button
        okButton = (Button) view.findViewById(R.id.okProfileButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(false);
                editButton.setVisibility(View.VISIBLE);
                //okButton.setVisibility(View.INVISIBLE);
                //cancelButton.setVisibility(View.INVISIBLE);
                buttonsView.setVisibility(View.INVISIBLE);

                // Set new values to update
                profile.setEmail(editEmail.getText().toString());
                profile.setInfo(editInfo.getText().toString());
                profile.setName(editName.getText().toString());
                profile.setPhone(editPhone.getText().toString());
                profile.setSurname(editSurname.getText().toString());

                updateProfile(profile);
            }
        });

        // Create listener to cancel button
        cancelButton = (Button) view.findViewById(R.id.cancelProfileButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(false);
                recoveryProfileInfo();
                editButton.setVisibility(View.VISIBLE);
                buttonsView.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    /**
     * Set opportunity to be active
     *
     * @param value
     *              The value to setting
     */
    private void setActiveEditTexts(boolean value){
        for (EditText edit: editTexts){
            if (value){
                edit.setFocusableInTouchMode(true);
            } else {
                edit.setFocusable(false);
            }
        }

    }

    /**
     * Temporarily storing profile information
     */
    private void saveProfileInfo(){
        name = editName.getText().toString();
        surname = editSurname.getText().toString();
        email = editEmail.getText().toString();
        phone = editPhone.getText().toString();
        info = editInfo.getText().toString();
    }

    /**
     * Recovery the saved profile information
     */
    private void recoveryProfileInfo(){
        editName.setText(name);
        editSurname.setText(surname);
        editEmail.setText(email);
        editPhone.setText(phone);
        editInfo.setText(info);
    }

    /**
     * Sets client for connect to service and get table from it
     *
     * @param client
     *               The client for connect to service
     */
    public void setClient(MobileServiceClient client){
        mClient = client;

        // Get the Mobile Service Table instance to use
        mProfileTable = mClient.getTable(Profile.class);
    }

    /**
     * Inserting a new user to mobile service
     */
    private void insertNewUser(){
        Profile newProfile = new Profile();
        newProfile.setId(mClient.getCurrentUser().getUserId());
        newProfile.setName(editName.getText().toString());
        newProfile.setSurname(editSurname.getText().toString());
        newProfile.setEmail(editEmail.getText().toString());
        newProfile.setPhone(editPhone.getText().toString());
        newProfile.setInfo(editInfo.getText().toString());

        mProfileTable.insert(newProfile, new TableOperationCallback<Profile>() {

            public void onCompleted(Profile entity, Exception exception, ServiceFilterResponse response) {

                if (exception == null) {
                    refreshProfile();
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                }
            }
        });
    }

    /**
     * Refreshes profile
     */
    public void refreshProfile(){

        // Get the profile and add information in the EditTexts
        mProfileTable.where().field("id").eq(mClient.getCurrentUser().getUserId())
                .execute(new TableQueryCallback<Profile>() {

                    @Override
                    public void onCompleted(List<Profile> result, int count, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {

                            if (result.size() == 0) {
                                insertNewUser();
                                return;
                            }

                            for (Profile item : result) {
                                editName.setText(item.getName());
                                editSurname.setText(item.getSurname());
                                editEmail.setText(item.getEmail());
                                editPhone.setText(item.getPhone());
                                editInfo.setText(item.getInfo());

                                profile = item;
                            }

                        } else {
                            Dialog.createAndShowDialog(exception, "error", getActivity());
                        }
                    }
                });
    }

    /**
     * Edit the profile
     */
    private void updateProfile(Profile profile) {
        if (mClient == null || profile == null) {
            return;
        }

        mProfileTable.update(profile, new TableOperationCallback<Profile>() {

            @Override
            public void onCompleted(Profile entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    Toast.makeText(getActivity(), "profile is updated", Toast.LENGTH_SHORT).show();
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                    recoveryProfileInfo();
                }
            }

        });
    }

}
