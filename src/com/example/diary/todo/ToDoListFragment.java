package com.example.diary.todo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.diary.DiaryActivity;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;
import java.util.List;

import static com.microsoft.windowsazure.mobileservices.MobileServiceQueryOperations.val;

/**
 * Created by Andrey on 07.04.2014.
 */
public class ToDoListFragment extends Fragment {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
    private ToDoItemAdapter mAdapter;

    /**
     * EditText containing the "New ToDo" text
     */
    private EditText mTextNewToDo;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layuot_to_do, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.loadingProgressBar);
        mTextNewToDo = (EditText) view.findViewById(R.id.textNewToDo);

        // Create listener to add button
        Button add = (Button) view.findViewById(R.id.buttonAddToDo);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });

        // Create an adapter to bind the items with the view
        mAdapter = new ToDoItemAdapter(getActivity(), R.layout.row_list_to_do, this);
        ListView listViewToDo = (ListView) view.findViewById(R.id.listViewToDo);
        listViewToDo.setAdapter(mAdapter);
        connect();
        return view;
    }

    public void connect(){
        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(DiaryActivity.appURL, DiaryActivity.appKey,
                    getActivity()).withFilter(new ToDoProgressFilter(getActivity(), mProgressBar));

            // Get the Mobile Service Table instance to use
            mToDoTable = mClient.getTable(ToDoItem.class);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error connecting the Mobile Service"), "Error");
        }

    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkItem(ToDoItem item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        mToDoTable.update(item, new TableOperationCallback<ToDoItem>() {

            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    if (entity.isComplete()) {
                        mAdapter.remove(entity);
                    }
                } else {
                    createAndShowDialog(exception, "Error");
                }
            }

        });
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        ToDoItem item = new ToDoItem();

        item.setText(mTextNewToDo.getText().toString());
        item.setComplete(false);

        // Insert the new item
        mToDoTable.insert(item, new TableOperationCallback<ToDoItem>() {

            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {

                if (exception == null) {
                    if (!entity.isComplete()) {
                        mAdapter.add(entity);
                    }
                } else {
                    createAndShowDialog(exception, "Error");
                }

            }
        });

        mTextNewToDo.setText("");
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    public void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter
        mToDoTable.where().field("complete").eq(val(false)).execute(new TableQueryCallback<ToDoItem>() {

            public void onCompleted(List<ToDoItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.clear();

                    for (ToDoItem item : result) {
                        mAdapter.add(item);
                    }

                } else {
                    createAndShowDialog(exception, "Error");
                }
            }
        });
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}