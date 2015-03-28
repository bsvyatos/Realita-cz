package com.demo.realita;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.demo.realita.R;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.Locale;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;


public class ListViewActivity extends Activity {

    //Azure mobile service instance
    private MobileServiceClient mClient;

    private ListView mListView;
    private HouseItemAdapter mHouseItemAdapter;
    MobileServiceTable<HouseItem> mHouseTable;

    //drawer navigation init
    private String [] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            mClient = new MobileServiceClient(
                    "https://realita-cz-demo.azure-mobile.net/",
                    "xEcspiKVLSUEJJaPRBEJnElbiQluGx23",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mHouseTable = mClient.getTable(HouseItem.class);

        if (mHouseTable == null) {
            Log.v("BIGERROR", "mHouseTalble was never received");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListView = (ListView) findViewById(R.id.myListView);

        mHouseItemAdapter = new HouseItemAdapter(getApplicationContext(), R.layout.row);

        showAll(mListView);
        if(mListView != null){
            mListView.setAdapter(mHouseItemAdapter);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent t = new Intent(view.getContext(), HouseInfoActivity.class);
                t.putExtra("hIndex", mHouseItemAdapter.getItem(i));
                startActivity(t);
            }
        });


        /*
        //push HouseItem
        mHouseTable.insert(myPlacesArray[0], new TableOperationCallback<HouseItem>() {
            @Override
            public void onCompleted(HouseItem entity, Exception exception, ServiceFilterResponse response) {
                if(exception == null){
                    //Insert succeeded;
                    Toast.makeText(getBaseContext(), "Successfuly added the item", Toast.LENGTH_LONG);
                } else {
                    //Insert failed;
                    Toast.makeText(getBaseContext(), "Failed to added the item", Toast.LENGTH_LONG);
                }
            }
        });
        */

        mMenuItems = getResources().getStringArray(R.array.sliding_menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerlist = (ListView) findViewById(R.id.left_drawer);

        //Set the adapter for list view for nav bar
        mDrawerlist.setAdapter(new ArrayAdapter<String>(this,
                R.layout.sliding_menu, mMenuItems));
        // Set the list's click listener
        mDrawerlist.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAll(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<HouseItem> result = mHouseTable.execute().get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mHouseItemAdapter.clear();
                            for (HouseItem item : result) {
                                mHouseItemAdapter.add(item);
                            }
                        }
                    });
                } catch (Exception e) {
                    //e.printStackTrace();
                    if (e == null) {
                        Log.e("e", "e is really null!!!");
                    }
                    else {
                        Log.e("e", "e is not null, toString is " + e + " and message is " + e.getMessage());
                    }
                }
                return null;
            }
        }.execute();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectItem(i);
        }
    }

    private void selectItem(int position){
        //Create a fragment and specify the the menu item based on the position

        Fragment fragment = new MenuItemFragment();
        Bundle args = new Bundle();
        args.putInt(MenuItemFragment.ARG_ITEM_NUMBER, position);

        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerlist.setItemChecked(position, true);
        setTitle(mMenuItems[position]);
        mDrawerLayout.closeDrawer(mDrawerlist);



    }

    public static class MenuItemFragment extends android.app.Fragment {
        public static final String ARG_ITEM_NUMBER = "planet_number";

        public MenuItemFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sliding_menu, container, false);
            int i = getArguments().getInt(ARG_ITEM_NUMBER);
            String menuitem = getResources().getStringArray(R.array.sliding_menu_list)[i];

            int imageId = getResources().getIdentifier(menuitem.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.menu_item_img)).setImageResource(imageId);
            getActivity().setTitle(menuitem);
            return rootView;
        }
    }
}

