package com.example.diary;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Create and show a warning dialogs
 */
public class Dialog {

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialog(Exception exception, String title, Activity activity) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title, activity);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialog(String message, String title, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}
