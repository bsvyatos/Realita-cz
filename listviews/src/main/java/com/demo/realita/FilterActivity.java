package com.demo.realita;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterActivity extends FragmentActivity
        implements FilterDialogFragment.NoticeDialogListener{
    Button bSave;
    Filter mFilter;
    EditText sMin;
    EditText sMax;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    private static final String TAG = ListViewActivity.class.getName();
    AutocompleteFilter mAutoFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        Intent intent = getIntent();
        mFilter = intent.getExtras().getParcelable("mFilter");

        setText(R.id.min_price, Integer.toString(mFilter.mPricemin) + " K?");
        LinearLayout pMinLayout = (LinearLayout) findViewById(R.id.MinPriceButton);
        pMinLayout.setOnClickListener(pMinOnClick);

        setText(R.id.max_price, Integer.toString(mFilter.mPricemax) + " K?");
        LinearLayout pMaxLayout = (LinearLayout) findViewById(R.id.MaxPriceButton);
        pMaxLayout.setOnClickListener(pMaxOnClick);

        sMin = (EditText) findViewById(R.id.size_min);
        sMin.setHint(Double.toString(mFilter.mSizemin));

        sMax = (EditText) findViewById(R.id.size_max);
        sMax.setHint(Double.toString(mFilter.mSizemax));

        bSave = (Button) findViewById(R.id.button_save);

        bSave.setOnClickListener(bHandler);

        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
    }

    View.OnClickListener bHandler = new View.OnClickListener() {
        public void onClick(View v) {

            try {
                mFilter.mSizemin = Integer.parseInt(sMin.getText().toString());
            } catch(NumberFormatException s){ }
            try {
                mFilter.mSizemax = Integer.parseInt(sMax.getText().toString());
            } catch(NumberFormatException s){ }

            Toast.makeText(getApplicationContext(), "Your preferences have been successfully saved", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("Filter", mFilter);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    };

    View.OnClickListener pMinOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNoticeDialog("Minimum Price", 0);
        }
    };

    View.OnClickListener pMaxOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNoticeDialog("Maximum Price", 1);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.action_settings == item.getItemId()){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showNoticeDialog(String msg, int mDialogState) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = FilterDialogFragment.newInstance(msg, "Ok", "Cancel", mDialogState);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String uInput, int mDialogState) {
        // User touched the dialog's positive button
        int mInput = Integer.parseInt(uInput);
        if(mDialogState == 0){
            mFilter.mPricemin = mInput;
            setText(R.id.min_price, Integer.toString(mFilter.mPricemin) + " K?");
        } else{
            mFilter.mPricemax = mInput;
            setText(R.id.max_price, Integer.toString(mFilter.mPricemax) + " K?");
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.dismiss();
    }

    public void setText(int res, String toThis){
        TextView v = (TextView) findViewById(res);
        v.setText(toThis);
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d(TAG, e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + R.string.password;

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }


}