package com.demo.realita;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class ListViewActivity extends BaseActivity {

    //Azure mobile service instance
    private MobileServiceClient mClient;

    private ListView mListView;
    private HouseItemAdapter mHouseItemAdapter;
    MobileServiceJsonTable mHouseTable;
    String fileName = "mFile";
    FavouriteArray FavArr;
    
    /*
    HouseItem newItem = new HouseItem("MyId007", "Kubelicetopkek 19", "Pronajem", "Byt", "3+1", "Osobni", "A", "N�zkoenergetick�"
            ,"Vybaven�", "Byt je za?�zen�; nov� kuchy?sk� linka a spor�k, sk?�n?, ledni?ka, kuchy?sk� st?l, postele, gau?\t"
            ,70, 4000000, 2500, 3, 15000, "wut?", "Info goes here, sometimes", false, false);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //get FavArr from shared preferences
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = mPrefs.getString("Favourites", "");
        FavArr = gson.fromJson(json, FavouriteArray.class);
        if(FavArr == null){
            List<String> fList = new ArrayList<String>();
            FavArr = new FavouriteArray(fList);
        }

        //First time application is started code
        if (!mPrefs.getBoolean("firstTime", false)) {
            //Write my newly created Filter to file
            try {
                FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                mFilter = new FilterBuilder().build();
                os.writeObject(mFilter);
                os.close();
                outputStream.close();
            } catch (Exception c) {
                c.printStackTrace();
            }
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        //Load my file from internal storage and get Filter
        try {
            FileInputStream fis = openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            mFilter = (Filter) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            mFilter = new FilterBuilder().build();
        }

        mFilter.qParam = 0;

        try {
            mClient = new MobileServiceClient(
                    "https://realita-cz-demo.azure-mobile.net/",
                    "xEcspiKVLSUEJJaPRBEJnElbiQluGx23",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mHouseTable = mClient.getTable("HouseItem");

        if (mHouseTable == null) {
            Log.v("RealitaCz", "mHouseTalble was never received");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        super.onCreateDrawer();

        mListView = (ListView) findViewById(R.id.myListView);

        mHouseItemAdapter = new HouseItemAdapter(getApplicationContext(), R.layout.row, FavArr);

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

                final JsonElement result =
                        mHouseTable.where().field("mBalkony").eq(mFilter.mBalkon)
                        .and().field("mPrice").ge(mFilter.mPricemin)
                        .and().field("mPrice").le(mFilter.mPricemax)
                        .and().field("mSize").ge(mFilter.mSizemin)
                        .and().field("mSize").le(mFilter.mSizemax)
                .execute().get();
                final JsonArray results = result.getAsJsonArray();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mHouseItemAdapter.clear();
                        for (JsonElement item : results) {
                            //This is terrible and it has no reason to be here This should be done in query instead
                            if ((mFilter.qParam == 2 &&
                                    FavArr.favList.contains(item.getAsJsonObject().getAsJsonPrimitive("id").getAsString())) || mFilter.qParam < 2) {
                                HouseItem house = new HouseItem(item);
                                mHouseItemAdapter.add(house);
                            }
                        }
                        if(mFilter.qParam == 2 && result == null){
                            //Favourites was pressed but there are currently no favourites, deal with it here
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

    @Override
    protected void onPause() {
        //save mFilter
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mFilter);
            os.close();
            fos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }

}

