package com.example.diary.todo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.diary.Dialog;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.util.List;

/**
 * Created by Andrey on 07.04.2014.
 */
public class ToDoListFragment extends Fragment {

    /**
     * Title of a warning dialog
     */
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

    private ToDoItem item;

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

        // Create listener to edit button
        Button edit = (Button) view.findViewById(R.id.buttonEditToDo);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = new EditText(getActivity());
                builder.setView(input);

                // Create listener to positive button of the edit dialog
                builder.setTitle("Edit notice");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editItem();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.setCancelable(true);
                builder.create().show();
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
     * Edit the text of item
     */
    public void editItem(){
        if (mClient == null || item == null) {
            return;
        }

        mToDoTable.update(item, new TableOperationCallback<ToDoItem>() {

            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    //mAdapter.remove(entity);
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