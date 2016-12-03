package com.chatt.gerdovci.chattapp;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pette.server.common.ChatMessage;

import java.util.ArrayList;
import java.util.TimerTask;


public class ItemDetailActivity extends ActionBarActivity {

    public static String IP_ADDRESS = "83.227.68.101";
    ListView msgListView;
    public static ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // Set up your ActionBar
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        //Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);


        Log.d("PETTE", "on create");

        final EditText msg = (EditText) findViewById(R.id.messageEditText);
        View messageEditText = findViewById(R.id.sendMessageButton);

        msgListView = (ListView) findViewById(R.id.msgListView);
        msgListView.setClickable(false);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        ArrayList<ChatMessage> chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatlist);
        msgListView.setAdapter(chatAdapter);

        messageEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Client client = new Client(chatAdapter, msg);
                client.execute();
            }
        });


        Log.d("PETTE", "we have done it");

        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                try {
                    Log.d("PETTE", "Trying to update convo");
                    UpdateConvo updateConvo = new UpdateConvo(chatAdapter);
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
                handler2.postDelayed(doAsynchronousTask, 400);
            }
           // Looper.prepare();
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
