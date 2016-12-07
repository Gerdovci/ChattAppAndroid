package com.chatt.gerdovci.chattapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pette.server.common.LoginRequest;
import com.pette.server.common.SendMessage;


/**
 * Created by gerdovci on 2016-12-02.
 */

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private LoginRequest myLoginRequest;
    public ArrayList<SendMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<SendMessage> list, LoginRequest loginRequest) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myLoginRequest = loginRequest;

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendMessage message = (SendMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chatbubble, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        msg.setText(message.getMessageBody());
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (message.getSenderName().endsWith(myLoginRequest.getUsername())) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
        }
        msg.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(SendMessage object) {
        chatMessageList.add(object);
    }

    public SendMessage getMessage(int pos) {
        return chatMessageList.get(pos);
    }
}