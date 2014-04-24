package com.example.diary.authentication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.diary.Dialog;
import com.example.diary.DiaryActivity;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

/**
 * Used for authentication
 */
public class AuthenticationFragment extends Fragment {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Creates view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_authentication, null);

        Button signInButton = (Button) view.findViewById(R.id.buttonSignInGoogle);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateGoogle( (DiaryActivity) getActivity());
            }
        });

        return view;
    }

    /**
     * Sets client for connect to service and get table from it
     *
     * @param client
     *               The client for connect to service
     */
    public void setClient(MobileServiceClient client){
        mClient = client;
    }

    /**
     * Authenticate the user with Google account
     *
     * @param activity
     *              The reference to current activity
     */
    private void authenticateGoogle(final DiaryActivity activity) {
        if (mClient == null) {
            return;
        }

        mClient.login(MobileServiceAuthenticationProvider.Google, new UserAuthenticationCallback() {
                    @Override
                    public void onCompleted(MobileServiceUser user, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            activity.showFirstFragment();
                        } else {
                            Dialog.createAndShowDialog("You must log in.", "Warning", activity);
                        }
                    }
                }
        );
    }
}
