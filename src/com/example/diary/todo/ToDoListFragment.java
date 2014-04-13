package com.example.diary.todo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.diary.Dialog;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.util.List;

/**
 * Fragment to showing and changing notices
 */
public class ToDoListFragment extends Fragment {

    /**
     * Title of a error dialog
     */
    private static final String error = "Error";

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

    /**
     * ListView containing the all notices
     */
    private ListView listViewToDo;

    /**
     * Button to deleting
     */
    private Button delButton;

    /**
     * Button to editing
     */
    private Button editButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layuot_to_do, null);
        mTextNewToDo = (EditText) view.findViewById(R.id.textNewToDo);

        // Create listener to add button
        Button addButton = (Button) view.findViewById(R.id.buttonAddToDo);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
                setButtonsEnable(false);
            }
        });

        // Create listener to edit button
        editButton = (Button) view.findViewById(R.id.buttonEditToDo);
        editButton.setEnabled(false);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // Get chosen item
                final ToDoItem item = mAdapter.getItem(listViewToDo.getCheckedItemPosition());

                final EditText input = new EditText(getActivity());
                input.setText(item.getText());
                builder.setView(input);

                // Create listener to positive button of the edit dialog
                builder.setTitle("Edit notice");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setButtonsEnable(false);
                        editItem(item, input.getText().toString());
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.setCancelable(true);
                builder.create().show();
            }
        });

        // Create listener to delete button
        delButton = (Button) view.findViewById(R.id.buttonDeleteToDo);
        delButton.setEnabled(false);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonsEnable(false);
                deleteItem(mAdapter.getItem(listViewToDo.getCheckedItemPosition()));
            }
        });

        // Create an adapter to bind the items with the view
        mAdapter = new ToDoItemAdapter(getActivity());
        listViewToDo = (ListView) view.findViewById(R.id.listViewToDo);
        listViewToDo.setAdapter(mAdapter);

        listViewToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setButtonsEnable(true);
            }
        });
        return view;
    }

    /**
     * Set value the editButton and the delButton to enable
     *
     * @param value
     *              The value to enable
     */
    private void setButtonsEnable(boolean value){
        delButton.setEnabled(value);
        editButton.setEnabled(value);
    }

    public void setClient(MobileServiceClient client){
        mClient = client;

        // Get the Mobile Service Table instance to use
        mToDoTable = mClient.getTable(ToDoItem.class);

        // Load the items from the Mobile Service
        refreshItemsFromTable();
    }

    /**
     * Delete chosen item
     *
     * @param item
     *            The item to mark
     */
    public void deleteItem(final ToDoItem item) {
        if (mClient == null || item == null) {
            return;
        }

        mToDoTable.delete(item, new TableDeleteCallback() {
            @Override
            public void onCompleted(Exception exception, ServiceFilterResponse serviceFilterResponse) {
                if (exception == null) {
                    mAdapter.remove(item);
                } else {
                    Dialog.createAndShowDialog(exception, error, getActivity());
                }
                setButtonsEnable(true);
            }
        });
    }

    /**
     * Edit the text of item
     */
    private void editItem(final ToDoItem item, String newText){
        if (mClient == null || item == null) {
            return;
        }

        item.setText(newText);

        mToDoTable.update(item, new TableOperationCallback<ToDoItem>() {

            @Override
            public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.remove(entity);
                    mAdapter.add(entity);
                    listViewToDo.setItemChecked(mAdapter.getPosition(entity), true);
                } else {
                    Dialog.createAndShowDialog(exception, error, getActivity());
                }
                setButtonsEnable(true);
            }

        });
    }

    /**
     * Add a new item
     */
    private void addItem() {
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
                    Dialog.createAndShowDialog(exception, error, getActivity());
                }
                setButtonsEnable(true);
            }
        });

        mTextNewToDo.setText("");
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    public void refreshItemsFromTable() {

        // Get the items and add them in the adapter
        mToDoTable.execute(new TableQueryCallback<ToDoItem>() {

            @Override
            public void onCompleted(List<ToDoItem> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.clear();

                    for (ToDoItem item : result) {
                        mAdapter.add(item);
                    }

                } else {
                    Dialog.createAndShowDialog(exception, error, getActivity());
                }
            }
        });
    }

}