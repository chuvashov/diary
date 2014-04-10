package com.example.diary.todo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.diary.Dialog;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.util.List;

/**
 * Created by Andrey on 07.04.2014.
 */
public class ToDoListFragment extends Fragment {

    private static final String warning = "Warning";

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
        return view;
    }

    public void setClient(MobileServiceClient client){
        mClient = client;

        // Get the Mobile Service Table instance to use
        mToDoTable = mClient.getTable(ToDoItem.class);

        // Load the items from the Mobile Service
        refreshItemsFromTable();
    }

    /*public void connect(){
        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(DiaryActivity.appURL, DiaryActivity.appKey,
                    getActivity()).withFilter(new ToDoProgressFilter(getActivity(), mProgressBar));
            mClient.withFilter(new ToDoProgressFilter(getActivity(), mProgressBar));
            // Get the Mobile Service Table instance to use
            mToDoTable = mClient.getTable(ToDoItem.class);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            Dialog.createAndShowDialog(new Exception("There was an error connecting the Mobile Service"),
                    "Error", getActivity());
        }

    }  */

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

        mToDoTable.update(item, new TableOperationCallback<ToDoItem>() {

            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.remove(entity);
                } else {
                    Dialog.createAndShowDialog(exception, warning, getActivity());
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

        // Insert the new item
        mToDoTable.insert(item, new TableOperationCallback<ToDoItem>() {

            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {

                if (exception == null) {
                    mAdapter.add(entity);
                } else {
                    Dialog.createAndShowDialog(exception, warning, getActivity());
                }

            }
        });

        mTextNewToDo.setText("");
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    public void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the adapter
        mToDoTable.execute(new TableQueryCallback<ToDoItem>() {

            public void onCompleted(List<ToDoItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.clear();

                    for (ToDoItem item : result) {
                        mAdapter.add(item);
                    }

                } else {
                    Dialog.createAndShowDialog(exception, warning, getActivity());
                }
            }
        });
    }

}