package com.chatt.gerdovci.chattapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gerdovci on 2016-12-07.
 */

public class ConversationListAdapter extends BaseAdapter  {

        private static LayoutInflater inflater = null;
        public ArrayList<ConversationListItems> conversationList;

        public ConversationListAdapter(Activity activity, ArrayList<ConversationListItems> list) {
            conversationList = list;
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return conversationList.size();
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
            ConversationListItems message = conversationList.get(position);
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.conversation_item, null);

            ImageView imageView = (ImageView) vi.findViewById(R.id.img_thumbnail);
            imageView.setImageResource(R.drawable.ic_launcher);


            TextView tetView = (TextView) vi.findViewById(R.id.message_sender);
            tetView.setText(message.getChattroomId());


            TextView tetView2 = (TextView) vi.findViewById(R.id.message_body);
            tetView2.setText(message.getMessage());

            return vi;
        }

        public void add(ConversationListItems object) {
            conversationList.add(object);
        }

}
