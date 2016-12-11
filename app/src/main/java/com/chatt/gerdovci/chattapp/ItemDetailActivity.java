package com.chatt.gerdovci.chattapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.pette.server.common.LoginRequest;
import com.pette.server.common.SendMessage;

import java.util.ArrayList;
import java.util.List;


public class ItemDetailActivity extends ActionBarActivity {

    public static String IP_ADDRESS = "83.227.68.101";
    public static int SERVER_PORT = 8080;
    ListView msgListView;
    public static ChatAdapter chatAdapter;
    static String username;
    static String password;
    static boolean mySlimUpdate = false;
    static SendMessage sendMessage = null;
    int mNotificationId = 001;
    static PendingIntent resultPendingIntent;
    static NotificationManager mNotifyMgr;

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("chat_active", true);
        ed.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sp = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("chat_active", false);
        ed.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setupActionBar();


        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String tempEmail = settings.getString("username", username);
        String tempPassword = settings.getString("password", password);

        if (tempEmail != null && tempEmail.length() > 0 && tempPassword != null && tempPassword.length() > 0) {
            username = tempEmail;
            password = tempPassword;
        } else {
            username = this.getIntent().getExtras().getString("username");
            password = this.getIntent().getExtras().getString("password");
        }

        Log.d("CHATAPP", "on create");

        final EditText msg = (EditText) findViewById(R.id.messageEditText);
        View messageEditText = findViewById(R.id.sendMessageButton);

        msgListView = (ListView) findViewById(R.id.msgListView);
        msgListView.setClickable(false);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);


        final LoginRequest loginRequest = new LoginRequest(username, password);

        ArrayList<SendMessage> chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatlist, loginRequest);
        msgListView.setAdapter(chatAdapter);

        messageEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Client client = new Client(chatAdapter, msg, loginRequest);
                client.execute();
            }
        });

        setupNotification();
        WorkerThread workerThread = new WorkerThread();
        workerThread.start();


        Thread runnable = new Thread() {
            @Override
            public void run() {
                Log.d("CHATAPP", "loop?");
                Handler mHandler = new Handler(Looper.getMainLooper());

                Log.d("CHATAPP", "Trying to update convo");
                UpdateConvo updateConvo = new UpdateConvo(chatAdapter, loginRequest);
                updateConvo.execute();
                mHandler.postDelayed(this, 2000);
                mHandler.sendEmptyMessage(1);
                generateNotification();
            }
        };

        runnable.start();
    }


    public void setupNotification() {
        Intent resultIntent = new Intent(this, MessageNotification.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MessageNotification.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void generateNotification() {

        if (mySlimUpdate && sendMessage != null) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(sendMessage.getSenderName())
                            .setContentText(sendMessage.getMessageBody());

            mBuilder.setContentIntent(resultPendingIntent);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }


        sendMessage = null;
        mySlimUpdate = false;
    }


    class WorkerThread extends Thread {
        public Handler mHandler;

        public void run() {
            Log.d("CHATAPP", "Preparing loop");
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


        Button homeButton = (Button) findViewById(R.id.btn_home);
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


        final Button optionButton = (Button) findViewById(R.id.btn_left);
        optionButton.setEnabled(true);

        optionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                PopupMenu popup = new PopupMenu(ItemDetailActivity.this, optionButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.options_list, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString().contains("Log out")) {

                            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            String tempPass = null;
                            editor.putString("password", tempPass);

                            // Commit the edits!
                            editor.commit();


                            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
                            intent.putExtra("INFORMATION", "");
                            startActivity(intent);
                        } else {

                            Toast.makeText(
                                    ItemDetailActivity.this,
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
    }


    public static void sendTextMessage(List<SendMessage> messages, boolean slimUpdate) {
        if (messages == null) {
            return;
        }

        if (slimUpdate) {
            mySlimUpdate = slimUpdate;
            if (messages.size() > 0) {
                SendMessage tempSendMessage = messages.get(messages.size() - 1);
                if (!tempSendMessage.getSenderName().equals(username)) {

                    sendMessage = tempSendMessage;
                }
            }
        }

        for (SendMessage chatMessage : messages) {
            if (!chatMessage.getMessageBody().equalsIgnoreCase("")) {
                chatAdapter.add(chatMessage);
            }
        }

        chatAdapter.notifyDataSetChanged();
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
