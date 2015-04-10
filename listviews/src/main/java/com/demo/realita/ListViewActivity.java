package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.demo.realita.R;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;


public class ListViewActivity extends BaseActivity {

    //Azure mobile service instance
    private MobileServiceClient mClient;
    private Filter mFilter;

    private ListView mListView;
    private HouseItemAdapter mHouseItemAdapter;
    MobileServiceTable<HouseItem> mHouseTable;

    /*
    HouseItem newItem = new HouseItem("MyId007", "Kubelicetopkek 19", "Pronajem", "Byt", "3+1", "Osobni", "A", "N�zkoenergetick�"
            ,"Vybaven�", "Byt je za?�zen�; nov� kuchy?sk� linka a spor�k, sk?�n?, ledni?ka, kuchy?sk� st?l, postele, gau?\t"
            ,70, 4000000, 2500, 3, 15000, "wut?", "Info goes here, sometimes", false, false);
    */

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
            Log.v("RealitaCz", "mHouseTalble was never received");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        super.onCreateDrawer();

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


        if (savedInstanceState == null) {
            selectItem(0);
        }


        /*
        //push HouseItem
        mHouseTable.insert(newItem, new TableOperationCallback<HouseItem>() {
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

        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                Intent t = new Intent(this, FilterActivity.class);
                t.putExtra("mFilter", mFilter);
                startActivity(t);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showAll(View view) {
        //Show everything, unfiltered
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Load/creat filter
                    Filter mFilter = LoadFilter();

                    final MobileServiceList<HouseItem> result;
                    //query
                    result = mHouseTable.where().field("mBalkony").eq(mFilter.mBalkon)
                                                    .add().field("mPrice").ge(mFilter.mPricemin)
                                                    .and().field("mPrice").le(mFilter.mPricemax)
                                                    .add().field("mSize").ge(mFilter.mSizemin)
                                                    .add().field("mSize").le(mFilter.mSizemax)
                                            .execute().get();

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
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public Filter LoadFilter(){
        //Creating a shared preference
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        //Retrieve Filter object from Shared Preferences if possible
        Gson gson =  new Gson();
        String json = mPrefs.getString("mFilter", "");
        mFilter = gson.fromJson(json, Filter.class);

        if(mFilter == null){
            mFilter = new FilterBuilder().build();
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            json = gson.toJson(mFilter);
            prefsEditor.putString("mFilter", json);
            prefsEditor.commit();
        }

        return mFilter;
    }
}

