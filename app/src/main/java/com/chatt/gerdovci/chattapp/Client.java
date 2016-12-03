package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pette.server.common.ChatMessage;

import static com.chatt.gerdovci.chattapp.ItemDetailActivity.chatAdapter;

public class Client extends AsyncTask<Void, Void, Void> {

    String response = "";
    ChatAdapter myChatAdapter;
    EditText myMsg;
    Socket socket = null;
    List<ChatMessage> messages;

    public Client(ChatAdapter chatAdapter, EditText msg) {
        myChatAdapter = chatAdapter;
        myMsg = msg;
        messages = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        socket = null;
        try {
            socket = new Socket(ItemDetailActivity.IP_ADDRESS, 8080);
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            // Send first message
            String sendText = "Hello pette";
            if (myMsg.getText() != null) {
                sendText = myMsg.getText().toString();
                sendText += "!#" +chatAdapter.getCount() ;
            }

            Log.d("PETTE", "send text:" + sendText);

            dOut.writeUTF(sendText);
            dOut.flush(); // Send off the data


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

         /*
          * notice: inputStream.read() will block if no data return
          */
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    inputStream);

            messages = (List<ChatMessage>) objectInputStream.readObject();

            objectInputStream.close();
            inputStream.close();
            dOut.close();


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void sendTextMessage(List<ChatMessage> messages) {
        if (messages == null) {
            return;
        }
        int even = 0;
        for (ChatMessage chatMessage : messages) {
            if (!chatMessage.body.equalsIgnoreCase("")) {

                if(even %2 == 0){
                    chatMessage.isMine = true;
                }else{
                    chatMessage.isMine = false;
                }

                chatAdapter.add(chatMessage);
                even++;
            }
        }
        chatAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPostExecute(Void result) {
        sendTextMessage(messages);
        myMsg.getText().clear();
        super.onPostExecute(result);
    }
}