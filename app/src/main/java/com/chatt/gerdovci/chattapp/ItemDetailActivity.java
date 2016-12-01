package com.chatt.gerdovci.chattapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


public class ItemDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.d("PETTE", "on create");

        final EditText msg = (EditText) findViewById(R.id.sendtext);
        final Button send = (Button) findViewById(R.id.bsend);
        final TextView convo = (TextView) findViewById(R.id.chattwindow);


        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Client client = new Client(convo, msg);
                client.execute();
            }
        });


        Log.d("PETTE", "we have done it");

        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                try {
                    Log.d("PETTE", "Trying to update convo");
                    UpdateConvo updateConvo = new UpdateConvo(convo);
                    updateConvo.execute();
                } catch (Exception e) {
                    Log.d("PETTE", e.getStackTrace().toString());
                    e.printStackTrace();
                }
            }
        };

        Log.d("PETTE", "Trying to schedule update convo");
        final Handler handler2 = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler2.postDelayed(doAsynchronousTask, 2000);
            }
        };
        runnable.run();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
