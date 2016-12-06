package com.chatt.gerdovci.chattapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.Toast;

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
        setupActionBar();


        Log.d("CHATAPP", "on create");

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




        WorkerThread workerThread = new WorkerThread();
        workerThread.start();


        Thread runnable = new Thread() {
            @Override
            public void run() {
                Log.d("CHATAPP","loop?");
                Handler mHandler = new Handler(Looper.getMainLooper());



                Log.d("CHATAPP", "Trying to update convo");
                UpdateConvo updateConvo = new UpdateConvo(chatAdapter);
                updateConvo.execute();
                mHandler.postDelayed(this, 2000);
                mHandler.sendEmptyMessage(1);
            }
        };

        runnable.start();
    }

    class WorkerThread extends Thread {
        public Handler mHandler;

        public void run() {
            Log.d("CHATAPP","Preparing loop");
            Looper.prepare();

            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            Looper.loop();
        }
    }

    private void setupActionBar() {
        // Set up your ActionBar
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        //Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);


        Button homeButton = (Button)findViewById(R.id.btn_home);
        homeButton.setEnabled(true);

        homeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Toast.makeText(ItemDetailActivity.this, "Not supported yet.",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(ItemDetailActivity.this, MainActivity.class);
                intent.putExtra("INFORMATION", "");
                startActivity(intent);
            }
        });
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
