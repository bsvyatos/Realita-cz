package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.delta.listviews.R;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

public class ListViewActivity extends Activity {

    //Azure mobile service instance
    private MobileServiceClient mClient;

    private ListView mListView;
    private HouseItemAdapter mHouseItemAdapter;
    MobileServiceTable<HouseItem> mHouseTable;

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




}

