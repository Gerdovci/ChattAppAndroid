package com.chatt.gerdovci.chattapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {


    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list);

        username = this.getIntent().getExtras().getString("username");
        password =  this.getIntent().getExtras().getString("password");
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
            public void onItemClick(AdapterView parent, View v, int position, long id){
               if(conversationListAdapter.getConversation(position).getChattroomId().equals("World chat")){
                   Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                   intent.putExtra("username", username);
                   intent.putExtra("password", password);
                   startActivity(intent);
               }
            }
        });

    }

}