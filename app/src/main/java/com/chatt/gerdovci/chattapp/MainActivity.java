package com.chatt.gerdovci.chattapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {


    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list);

        username = this.getIntent().getExtras().getString("username");
        Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();


        ListView msgListView = (ListView) findViewById(R.id.conversationList);
        msgListView.setClickable(false);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        ArrayList<ConversationListItems> chatlist = new ArrayList<>();
        ConversationListAdapter conversationListAdapter = new ConversationListAdapter(this, chatlist);
        msgListView.setAdapter(conversationListAdapter);

        ConversationListItems conversationListItems = new ConversationListItems("", "Petrit Gerdovci", "The King Gorilla");

        ConversationListItems conversationListItems2 = new ConversationListItems("", "Sebbe B", "Pebbe");
        conversationListAdapter.add(conversationListItems);
        conversationListAdapter.add(conversationListItems2);
    }

}