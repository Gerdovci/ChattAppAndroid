package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */

import android.os.AsyncTask;
import android.util.Log;
import com.pette.server.common.SendMessage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.chatt.gerdovci.chattapp.ItemDetailActivity.chatAdapter;

public class UpdateConvo extends AsyncTask<Void, Void, Void> {

    String response = "";
    ChatAdapter myChatAdapter;
    List<SendMessage> messages = new ArrayList<>();

    public UpdateConvo(ChatAdapter chatAdapter) {
        myChatAdapter = chatAdapter;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;
        try {
            socket = new Socket(ItemDetailActivity.IP_ADDRESS, 8080);
            Log.d("PETTE", "in here");


            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            // Send first message
            //dOut.writeByte(1);
            String sendText="GET UPDATECONVO";

            sendText +=  "!#" +  chatAdapter.getCount();
            Log.d("CHATAPP", "send text:" + sendText);
            dOut.writeUTF(sendText);
            dOut.flush(); // Send off the data

            InputStream inputStream = socket.getInputStream();


            if (inputStream.available() == -1) {
                socket.close();
                Log.d("PETTE", "closing down socket");
                return null;
            }

            Log.d("CHATAPP", "Reading stream now.");
            socket.setSoTimeout(400);

            Log.d("CHATAPP", "Reading");

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    inputStream);

            messages = (List<SendMessage>) objectInputStream.readObject();

            objectInputStream.close();
            inputStream.close();

        } catch (SocketTimeoutException e) {
            Log.d("CHATAPP", e.toString());
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

    @Override
    protected void onPostExecute(Void result) {
        Client.sendTextMessage(messages);
        super.onPostExecute(result);
    }

}