package com.demo.realita;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Svyatoslav on 30-Mar-15.
 */
public class BaseActivity extends Activity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mItemTitles;
    private CustomDrawerAdapter mItemAdapter;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<DrawerItem> navDrawerItems;
    public Filter mFilter;
    public String[] HDispositions;
    public String[] HEstateType;
    public String[] HOwnership;
    public String[] HConstruction;
    public String[] HEquipment;

    private void setEnums() {
        String[] mItems = getResources().getStringArray(R.array.OfferType);
        int i = 0;
        for (OfferType n : OfferType.values()) {
            n.setString(mItems[i++]);
        }

        mItems = getResources().getStringArray(R.array.EstateType);
        i = 0;
        for (EstateType n : EstateType.values()) {
            n.setString(mItems[i++]);
        }

        HDispositions = getResources().getStringArray(R.array.Disposition);
        HEstateType = getResources().getStringArray(R.array.EstateType);
        HOwnership = getResources().getStringArray(R.array.Ownership);
        HConstruction = getResources().getStringArray(R.array.Construction);
        HEquipment = getResources().getStringArray(R.array.Equipment);
    }

    protected void onCreateDrawer() {
        setEnums();
        mTitle = mDrawerTitle = getTitle();
        mItemTitles = getResources().getStringArray(R.array.sliding_menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.sliding_menu_list);
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.icons_menu_list);

        navDrawerItems = new ArrayList<DrawerItem>();
        for(int i=0; i<6 ;i++){
            Drawable d = getResources().getDrawable(navMenuIcons.getResourceId(i, -1));
            //invert Icon colours
            InvertColour(d);
            //add to array
            navDrawerItems.add(new DrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        mItemAdapter = new CustomDrawerAdapter(this, R.layout.drawer_list_item, navDrawerItems);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(mItemAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons here

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            drawerHandler(position);
        }
    }

    private void drawerHandler(int pos){
        switch(pos){
            case 0:
                mFilter.qParam = 0;
                mFilter.mOfferType = OfferType.SALE;
            case 1:
                mFilter.mOfferType = OfferType.RENT;
            case 2:
                //sell
                return;
            case 3:
                //favourite
                mFilter.qParam = 2;
            case 4:
                //Notifications
                return;
            case 5:
                //Sign/Log in
                return;
        }

        return;
    }

    protected void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ItemFragment.ARG_ITEM_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mItemTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */

    public static class ItemFragment extends Fragment {
        public static final String ARG_ITEM_NUMBER = "item_number";

        public ItemFragment() {
            // Empty constructor required for fragment subclasses
        }

    }

    public void InvertColour(Drawable img){
        //To generate negative image
        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };

        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);

        img.setColorFilter(colorFilter_Negative);

    }


}

