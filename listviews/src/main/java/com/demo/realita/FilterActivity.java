package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by Svyatoslav on 08-Apr-15.
 */
public class FilterActivity extends Activity {
    Button bSave;
    Filter mFilter;
    EditText pMin;
    EditText pMax;
    EditText sMin;
    EditText sMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        Intent intent = getIntent();
        mFilter = intent.getExtras().getParcelable("mFilter");

        pMin = (EditText) findViewById(R.id.price_min);
        pMin.setHint(Integer.toString(mFilter.mPricemin));

        pMax = (EditText) findViewById(R.id.price_max);
        pMax.setHint(Integer.toString(mFilter.mPricemax));

        sMin = (EditText) findViewById(R.id.size_min);
        sMin.setHint(Double.toString(mFilter.mSizemin));

        sMax = (EditText) findViewById(R.id.size_max);
        sMax.setHint(Double.toString(mFilter.mSizemax));

        bSave = (Button) findViewById(R.id.button_save);

        bSave.setOnClickListener(bHandler);

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
    }

    View.OnClickListener bHandler = new View.OnClickListener() {
        public void onClick(View v) {

            try {
                mFilter.mPricemin = Integer.parseInt(pMin.getText().toString());
            } catch(NumberFormatException s){
                Toast.makeText(getApplicationContext(), "Exception with Minimum Price field pMin=" + pMin.toString(), Toast.LENGTH_LONG).show();
            }
            try {
                mFilter.mPricemax = Integer.parseInt(pMax.getText().toString());
            } catch(NumberFormatException s){ }
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
}