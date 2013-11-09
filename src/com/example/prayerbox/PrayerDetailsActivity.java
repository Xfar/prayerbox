package com.example.prayerbox;

import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PrayerDetailsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pray_detail);
        TextView txtRequest = (TextView) findViewById(R.id.prayer_request);
        txtRequest.setMovementMethod(new ScrollingMovementMethod());
        
        Intent i = getIntent();
        // Receiving the Data
        String subject = i.getStringExtra("subject");
        String request = i.getStringExtra("request");
 
        // Displaying Received data
        setTitle(subject);
        txtRequest.setText(request);
    }
}
