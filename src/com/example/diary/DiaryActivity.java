package com.example.diary;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.diary.authentication.AuthenticationFragment;
import com.example.diary.diary.DiaryFragment;
import com.example.diary.profile.ProfileFragment;
import com.example.diary.todo.ToDoListFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

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
     * Title for warning dialogs
     */
    private static final String warning = "Warning";

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Sliding menu used to show app
     */
    private SlidingMenu menu;

    /**
     * Fragment Transaction used to exchange fragments
     */
    private FragmentTransaction fTrans;

    /**
     * Fragments
     */
    private ToDoListFragment listFragment;
    private ProfileFragment profileFragment;
    private DiaryFragment diaryFragment;

    /**
     * States pointed that fragment shown
     */
    private static final int AUTHENTICATION = -1;
    private static final int PROFILE = 0;
    private static final int DIARY = 1;
    private static final int TO_DO_LIST = 2;

    /**
     * Current state pointed that fragment shown now
     */
    private int currentFragment;

	/**
     * Initializes the activity
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Initialize progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main_layout);
        setProgressBarIndeterminateVisibility(false);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int k = metrics.densityDpi / 160;
        int offset;
        if (metrics.widthPixels * k <= 460) {
            offset = 60 / k;
        } else {
            offset = metrics.widthPixels * k - 400;
        }

        // Create and setting Sliding Menu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffset(offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu);

        menu.setSlidingEnabled(false);

        ListView menuList = (ListView) findViewById(R.id.menuListView);
        final String[] menuItems = getResources().getStringArray(R.array.menu_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_expandable_list_item_1, menuItems);

        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentFragment) {
                    fTrans = getFragmentManager().beginTransaction();
                    switch (position) {
                        case TO_DO_LIST:
                            currentFragment = TO_DO_LIST;
                            fTrans.replace(R.id.mainFrameLayout, listFragment).commit();
                            listFragment.refreshItems();
                            getActionBar().setTitle(R.string.to_do_list);
                            break;
                        case PROFILE:
                            currentFragment = PROFILE;
                            fTrans.replace(R.id.mainFrameLayout, profileFragment).commit();
                            profileFragment.refreshProfile();
                            getActionBar().setTitle(R.string.profile);
                            break;
                        case DIARY:
                            currentFragment = DIARY;
                            fTrans.replace(R.id.mainFrameLayout, diaryFragment).commit();
                            diaryFragment.refreshItems();
                            getActionBar().setTitle(R.string.diary);
                            break;
                    }

                }
                menu.toggle(true);
            }
        });

        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        AuthenticationFragment authenticationFragment = new AuthenticationFragment();

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(appURL, appKey, this).withFilter(
                    new DiaryProgressFilter(this));

            authenticationFragment.setClient(mClient);

            currentFragment = AUTHENTICATION;
            fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.mainFrameLayout, authenticationFragment);
            fTrans.commit();
            getActionBar().setTitle(R.string.authentication);

        } catch (MalformedURLException e) {
            Dialog.createAndShowDialog(new Exception("There was an error connecting the Mobile Service"),
                    warning, this);
        }

        // Initialize fragments
        profileFragment = new ProfileFragment();
        listFragment = new ToDoListFragment();
        diaryFragment = new DiaryFragment();

        profileFragment.setClient(mClient);
        listFragment.setClient(mClient);
        diaryFragment.setClient(mClient);

    }

    /**
     * Begins to show the fragments
     */
    public void showFirstFragment(){

        // Show the first fragment
        currentFragment = DIARY;
        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.mainFrameLayout, diaryFragment);
        fTrans.commit();
        getActionBar().setTitle(R.string.diary);
        diaryFragment.refreshItems();

        menu.setSlidingEnabled(true);

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
                if (currentFragment == AUTHENTICATION) {
                    return false;
                }
                menu.toggle(true);
                return true;
            case R.id.menu_refresh:
                this.refresh();
                if (menu == null) {
                    return false;
                }
                if (menu.isMenuShowing()){
                    menu.toggle(true);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes fragment loaded content
     */
    private void refresh(){
        switch (currentFragment) {
            case TO_DO_LIST:
                listFragment.refreshItems();
                break;
            case PROFILE:
                profileFragment.refreshProfile();
                break;
            case DIARY:
                diaryFragment.refreshItems();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (menu == null) {
                return super.onKeyDown(keyCode, event);
            }
            if (menu.isMenuShowing()) {
                menu.toggle(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Log outs from user's account
     *
     * @param view
     */
    public void logout(View view){
        mClient.logout();
        currentFragment = AUTHENTICATION;

        menu.setSlidingEnabled(false);
        menu.toggle(true);

        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        AuthenticationFragment authenticationFragment = new AuthenticationFragment();
        authenticationFragment.setClient(mClient);

        fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.mainFrameLayout, authenticationFragment).commit();

        getActionBar().setTitle(R.string.authentication);

        CookieSyncManager.createInstance(this);
        CookieManager manager = CookieManager.getInstance();
        manager.removeAllCookie();

    }

}
