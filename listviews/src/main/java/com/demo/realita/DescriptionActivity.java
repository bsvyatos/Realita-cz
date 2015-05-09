package com.demo.realita;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Svyatoslav on 09-May-15.
 */
public class DescriptionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent t = getIntent();
        String mDescription = t.getStringExtra("Description");
        setContentView(R.layout.description_layout);

        TextView descView = (TextView) findViewById(R.id.DescView);
        descView.setText(mDescription);
    }
}
