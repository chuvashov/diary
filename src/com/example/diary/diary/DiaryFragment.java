package com.example.diary.diary;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.diary.Dialog;
import com.example.diary.R;
import com.microsoft.windowsazure.mobileservices.*;

import java.util.List;


public class DiaryFragment extends Fragment {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Diary> mDiaryTable;

    /**
     * Adapter to sync the items list with the view
     */
    private DiaryAdapter mAdapter;

    /**
     * ListView containing the all records
     */
    private ListView listViewDiary;

    /**
     * Button to deleting
     */
    private Button delButton;

    /**
     * Button to editing
     */
    private Button editButton;

    /**
     * Creates view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_diary, null);

        final DiaryDialogBuilder diaryDialog = new DiaryDialogBuilder();

        // Create listener to add button
        final DialogInterface.OnClickListener addListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setButtonsEnable(false);
                Diary newDiary = new Diary();

                newDiary.setTitle(diaryDialog.getTitle());
                newDiary.setText(diaryDialog.getRecordText());
                newDiary.setDate(diaryDialog.getDate());
                newDiary.setUserId(mClient.getCurrentUser().getUserId());
                addItem(newDiary);
            }
        };

        Button addButton = (Button) view.findViewById(R.id.buttonAddDiary);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryDialog.clear();
                diaryDialog.createAndShow(addListener, getActivity());
                setButtonsEnable(false);
            }
        });

        // Create listener to delete button
        delButton = (Button) view.findViewById(R.id.buttonDeleteDiary);
        delButton.setEnabled(false);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonsEnable(false);
                if (listViewDiary.getCheckedItemPosition() < 0 ||
                        listViewDiary.getCheckedItemPosition() >= mAdapter.getCount()) {
                    return;
                }

                deleteItem(mAdapter.getItem(listViewDiary.getCheckedItemPosition()));
            }
        });

        // Create listener to edit button
        final DialogInterface.OnClickListener editListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setButtonsEnable(false);
                Diary chosenRecord = mAdapter.getItem(listViewDiary.getCheckedItemPosition());

                chosenRecord.setTitle(diaryDialog.getTitle());
                chosenRecord.setText(diaryDialog.getRecordText());
                chosenRecord.setDate(diaryDialog.getDate());
                chosenRecord.setUserId(mClient.getCurrentUser().getUserId());

                editItem(chosenRecord);
            }
        };

        editButton = (Button) view.findViewById(R.id.buttonEditDiary);
        editButton.setEnabled(false);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryDialog.clear();
                Diary chosenRecord = mAdapter.getItem(listViewDiary.getCheckedItemPosition());
                diaryDialog.setTitle(chosenRecord.getTitle());
                diaryDialog.setRecordText(chosenRecord.getText());
                diaryDialog.setDate(chosenRecord.getDate());

                diaryDialog.createAndShow(editListener, getActivity());
            }
        });

        // Create an adapter to bind the items with the view
        mAdapter = new DiaryAdapter(getActivity());
        listViewDiary = (ListView) view.findViewById(R.id.listViewDiary);
        listViewDiary.setAdapter(mAdapter);

        listViewDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    /**
     * Set client for connect to service and get table from it
     *
     * @param client
     *               The client for connect to service
     */
    public void setClient(MobileServiceClient client){
        mClient = client;

        // Get the Mobile Service Table instance to use
        mDiaryTable = mClient.getTable(Diary.class);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */
    public void refreshItems() {

        // Get the items and add them in the adapter
        mDiaryTable.where().field("user_id").eq(mClient.getCurrentUser().getUserId())
                .execute(new TableQueryCallback<Diary>() {

            @Override
            public void onCompleted(List<Diary> result, int count, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.clear();

                    for (Diary item : result) {
                        mAdapter.add(item);
                    }

                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                }
            }
        });
    }

    /**
     * Delete chosen item
     *
     * @param item
     *            The item to delete
     */
    public void deleteItem(final Diary item) {
        if (mClient == null || item == null) {
            return;
        }

        mDiaryTable.delete(item, new TableDeleteCallback() {
            @Override
            public void onCompleted(Exception exception, ServiceFilterResponse serviceFilterResponse) {
                if (exception == null) {
                    mAdapter.remove(item);
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                }
                activeButtons();
            }
        });
    }

    /**
     * Edit the text of item
     */
    private void editItem(final Diary item){
        if (mClient == null || item == null) {
            return;
        }

        mDiaryTable.update(item, new TableOperationCallback<Diary>() {

            @Override
            public void onCompleted(Diary entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    mAdapter.remove(entity);
                    mAdapter.add(entity);
                    listViewDiary.setItemChecked(mAdapter.getPosition(entity), true);
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                }
                activeButtons();
            }

        });
    }

    /**
     * Add a new item
     */
    private void addItem(Diary item) {
        if (mClient == null) {
            return;
        }

        // Insert the new item
        mDiaryTable.insert(item, new TableOperationCallback<Diary>() {

            public void onCompleted(Diary entity, Exception exception, ServiceFilterResponse response) {

                if (exception == null) {
                    mAdapter.add(entity);
                } else {
                    Dialog.createAndShowDialog(exception, "error", getActivity());
                }
                activeButtons();
            }
        });
    }

    /**
     * If item is chosen make edit and delete buttons enable,
     * otherwise make them disable
     */
    private void activeButtons() {
        if (listViewDiary.getCheckedItemPosition() < 0 ||
                listViewDiary.getCheckedItemPosition() >= mAdapter.getCount()) {
            setButtonsEnable(false);
        } else {
            setButtonsEnable(true);
        }
    }

}
