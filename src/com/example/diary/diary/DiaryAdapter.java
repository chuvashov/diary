package com.example.diary.diary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.diary.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Adapter to bind a Diary List to a view
 */
public class DiaryAdapter extends ArrayAdapter<Diary> {

    /**
     * Adapter context
     */
    private Context mContext;

    /**
     * Adapter DateFormat
     */
    private SimpleDateFormat dateFormat;

    /**
     * Adapter View layout
     */
    private static final int mLayoutResourceId = R.layout.row_list_diary;

    public DiaryAdapter(Context context) {
        super(context, R.layout.row_list_diary);
        mContext = context;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Diary currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_list_diary, parent, false);
        }

        row.setTag(currentItem);

        // Sets title
        final TextView titleView = (TextView) row.findViewById(R.id.viewTitleDiaryItem);
        titleView.setText(currentItem.getTitle());

        // Sets text
        final TextView textView = (TextView) row.findViewById(R.id.viewTextDiaryItem);
        textView.setText(currentItem.getText());

        // Sets date
        final TextView dateView = (TextView) row.findViewById(R.id.viewDateDiaryItem);
        if (currentItem.getDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentItem.getDate());
            dateView.setText(dateFormat.format(currentItem.getDate()));
        } else {
            dateView.setText("");
        }

        textView.setEnabled(true);

        return row;
    }
}
