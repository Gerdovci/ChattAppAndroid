package com.chatt.gerdovci.chattapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.phoneNumber;


public class MainActivity extends ActionBarActivity {


    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list);

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

        Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();


        ListView msgListView = (ListView) findViewById(R.id.conversationList);
        msgListView.setClickable(false);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        ArrayList<ConversationListItems> chatlist = new ArrayList<>();
        final ConversationListAdapter conversationListAdapter = new ConversationListAdapter(this, chatlist);
        msgListView.setAdapter(conversationListAdapter);

        ConversationListItems conversationListItems = new ConversationListItems("", "Petrit Gerdovci", "The King Gorilla");

        ConversationListItems conversationListItems2 = new ConversationListItems("", "Sebbe B", "Pebbe");
        ConversationListItems conversationListItems3 = new ConversationListItems("", "World chat", "Some conversation text");

        conversationListAdapter.add(conversationListItems3);
        conversationListAdapter.add(conversationListItems);
        conversationListAdapter.add(conversationListItems2);


        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if (conversationListAdapter.getConversation(position).getChattroomId().equals("World chat")) {
                    Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });

    }

    private void setupActionBar() {
        // Set up your ActionBar
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        //Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.menu_action_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);


        final EditText editText = (EditText) findViewById(R.id.search_field);
        editText.setVisibility(View.INVISIBLE);

        ImageView imageView = (ImageView) findViewById(R.id.search_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
            }
        });
    }
}