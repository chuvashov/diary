package com.example.diary.todo;

import android.app.Activity;
import android.widget.ProgressBar;
import com.microsoft.windowsazure.mobileservices.*;

/**
 * Created by Andrey on 07.04.2014.
 */
public class ToDoProgressFilter implements ServiceFilter {

    private ProgressBar mProgressBar;
    private Activity activity;

    public ToDoProgressFilter(Activity activity, ProgressBar progressBar) {
        mProgressBar = progressBar;
        this.activity = activity;
    }

    @Override
    public void handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback,
                              final ServiceFilterResponseCallback responseCallback) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            }
        });

        nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    }
                });

                if (responseCallback != null){
                    responseCallback.onResponse(response, exception);
                }
            }
        });
    }
}
