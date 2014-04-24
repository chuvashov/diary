package com.example.diary;

import android.app.Activity;
import com.microsoft.windowsazure.mobileservices.*;

/**
 * Shows loading progress
 */
public class DiaryProgressFilter implements ServiceFilter {

    /**
     * Activity reference
     */
    private Activity activity;

    /**
     * DiaryProgressFilter constructor
     *
     * @param activity
     *                The activity reference
     */
    public DiaryProgressFilter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
                              final ServiceFilterResponseCallback responseCallback) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                activity.setProgressBarIndeterminateVisibility(true);
            }
        });

        nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.setProgressBarIndeterminateVisibility(false);
                    }
                });

                if (responseCallback != null){
                    responseCallback.onResponse(response, exception);
                }
            }
        });
    }
}
