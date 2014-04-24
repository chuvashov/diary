package com.example.diary.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.example.diary.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Create dialog builder to adding and editing a diary records
 */
public class DiaryDialogBuilder {

    /**
     * Builder reference
     */
    private AlertDialog.Builder builder;

    /**
     * EditText used to edit the title
     */
    private EditText titleEditor;

    /**
     * EditText used to edit the text
     */
    private EditText textEditor;

    /**
     * Button used to changing date
     */
    private Button dateButton;

    /**
     * The date of diary record
     */
    private Date date;

    /**
     * String containing title
     */
    private String title;

    /**
     * String containing text
     */
    private String text;

    /**
     * SimpleDateFormat reference
     */
    private SimpleDateFormat dateFormat;

    /**
     *  DiaryDialogBuilder constructor
     */
    public DiaryDialogBuilder(){
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Sets the title
     *
     * @param title
     *             The title to setting
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Returns title of the diary record
     */
    public String getTitle(){
        if (titleEditor != null){
            return titleEditor.getText().toString();
        }
        return title;
    }

    /**
     * Sets the diary record text
     *
     * @param text
     *          The text to setting
     */
    public void setRecordText(String text){
        this.text = text;
    }

    /**
     * Returns the diary record text
     */
    public String getRecordText(){
        if (textEditor != null) {
            return textEditor.getText().toString();
        }
        return text;
    }

    /**
     * Sets a date
     *
     * @param date
     *          The date to setting
     */
    public void setDate(Date date){
        this.date = date;
    }

    /**
     * Returns the date
     */
    public Date getDate(){
        return date;
    }

    /**
     * Clears values
     */
    public void clear(){
        title = "";
        text = "";
        date = new Date(System.currentTimeMillis());
        titleEditor = null;
        textEditor = null;
        dateButton = null;
    }

    /**
     * Creates and shows dialog
     *
     * @param positiveListener
     *              The listener to positive button of the dialog
     * @param activity
     *              The reference to current activity
     */
    public void createAndShow(DialogInterface.OnClickListener positiveListener, final Activity activity){
        builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialod_diary_layout, null);

        titleEditor = (EditText) dialogView.findViewById(R.id.titleDialogView);
        titleEditor.setText(title);

        textEditor = (EditText) dialogView.findViewById(R.id.textDialogView);
        textEditor.setText(text);

        dateButton = (Button) dialogView.findViewById(R.id.buttonDateDiaryDialog);
        if (date == null){
            dateButton.setText(R.string.sets_date);
        } else {
            dateButton.setText(dateFormat.format(date));
        }

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                date = calendar.getTime();
                dateButton.setText(dateFormat.format(date));
            }
        };

        // Create listener to date setting button
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date == null) {
                    date = new Date(System.currentTimeMillis());
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                DatePickerDialog dialog = new DatePickerDialog(activity, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        builder.setView(dialogView);

        builder.setTitle("Diary record");

        builder.setPositiveButton("OK", positiveListener);

        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(true);

        builder.create().show();
    }
}
