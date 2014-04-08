package com.example.diary;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.diary.todo.ToDoListFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class DiaryActivity extends Activity {

    /**
     * App appURL to connect
     */
    public static final String appURL = "https://diary.azure-mobile.net/";

    /**
     * App key to connect
     */
    public static final String appKey = "WsbGbXpVcLkvxwvrWFsUxLWHFOQgkP36";

    /**
     * Sliding menu used to show app
     */
    private SlidingMenu menu;

    /**
     * Fragment Transaction used to exchange fragments
     */
    private FragmentTransaction fTrans;

    private ToDoListFragment listFragment;

    private static final int TO_DO_LIST = 2;

    private int curMenuItem;

	/**
	 * Initializes the activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Initialize fragments
        listFragment = new ToDoListFragment();

        // Show the first fragment
        curMenuItem = TO_DO_LIST;
        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.mainFrameLayout, listFragment);
        fTrans.commit();

        // Create and setting Sliding Menu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);

        ListView menuList = (ListView) findViewById(R.id.menuListView);
        final String[] menuItems = getResources().getStringArray(R.array.menu_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_expandable_list_item_1, menuItems);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != curMenuItem) {
                    fTrans = getFragmentManager().beginTransaction();
                    switch (position) {
                        case TO_DO_LIST:
                            fTrans.replace(R.layout.layuot_to_do, listFragment);
                            break;

                    }
                    fTrans.commit();
                }
                menu.toggle(true);
            }
        });

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menu.toggle(true);
                return true;
            case R.id.menu_refresh:
                refresh();
                if (menu.isMenuShowing()){
                    menu.toggle(true);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        switch (curMenuItem){
            case TO_DO_LIST:
                listFragment.refreshItemsFromTable();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(menu.isMenuShowing()){
                menu.toggle(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
