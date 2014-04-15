package com.example.diary.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText editName;
    private EditText editSurname;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editInfo;

    private Button editButton;
    private Button okButton;
    private Button cancelButton;

    private String phone;
    private String email;
    private String name;
    private String surname;
    private String info;

    private Profile profile;

    private List<EditText> editTexts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        setActiveEditTexts(false);

        // Create listener to edit button
        editButton = (Button) view.findViewById(R.id.editProfileButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(true);
                saveProfileInfo();
                editButton.setVisibility(View.INVISIBLE);
                okButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            }
        });

        // Create listener to ok button
        okButton = (Button) view.findViewById(R.id.okProfileButton);
        okButton.setVisibility(View.INVISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(false);
                editButton.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);

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
        cancelButton.setVisibility(View.INVISIBLE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveEditTexts(false);
                reestablishProfileInfo();
                editButton.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void setActiveEditTexts(boolean value){
        for (EditText edit: editTexts){
            if (value){
                edit.setFocusableInTouchMode(value);
            } else {
                edit.setFocusable(value);
            }
        }

    }

    private void saveProfileInfo(){
        name = editName.getText().toString();
        surname = editSurname.getText().toString();
        email = editEmail.getText().toString();
        phone = editPhone.getText().toString();
        info = editInfo.getText().toString();
    }

    private void reestablishProfileInfo(){
        editName.setText(name);
        editSurname.setText(surname);
        editEmail.setText(email);
        editPhone.setText(phone);
        editInfo.setText(info);
    }

    public void setClient(MobileServiceClient client){
        mClient = client;

        // Get the Mobile Service Table instance to use
        mProfileTable = mClient.getTable(Profile.class);

        // Load the profile info from the Mobile Service
        refresh();
    }

    public void refresh(){

        // Get the profile and add information in the EditTexts
        mProfileTable.execute(new TableQueryCallback<Profile>() {

            @Override
            public void onCompleted(List<Profile> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {

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
    private void updateProfile(Profile profile){
        if (mClient == null || profile == null) {
            return;
        }

        mProfileTable.update(profile, new TableOperationCallback<Profile>() {

            @Override
            public void onCompleted(Profile entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    Dialog.createAndShowDialog("Profile is updated", "Done", getActivity());
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                    reestablishProfileInfo();
                }
            }

        });
    }

}
