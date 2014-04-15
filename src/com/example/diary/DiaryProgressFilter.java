package com.example.diary;

import android.app.Activity;
import com.microsoft.windowsazure.mobileservices.*;

/**
 * Created by Andrey on 07.04.2014.
 */
public class DiaryProgressFilter implements ServiceFilter {

    private Activity activity;

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
