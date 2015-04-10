package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        Intent intent = getIntent();
        mFilter = intent.getExtras().getParcelable("mFilter");
        Button bSave = (Button) findViewById(R.id.button_save);
        bSave.setOnClickListener(bHandler);

    }

    View.OnClickListener bHandler = new View.OnClickListener() {
        public void onClick(View v) {
            EditText pMin = (EditText) findViewById(R.id.price_min);
            pMin.getText();

            EditText pMax = (EditText) findViewById(R.id.price_max);
            pMin.getText();

            EditText sMin = (EditText) findViewById(R.id.size_min);
            pMin.getText();

            EditText sMax = (EditText) findViewById(R.id.size_max);
            pMin.getText();

            Gson gson = new Gson();
            SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            String json = gson.toJson(mFilter);
            prefsEditor.putString("mFilter", json);
            prefsEditor.commit();
            Toast.makeText(getBaseContext(), "Your preferences have been successfully saved", Toast.LENGTH_LONG);
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