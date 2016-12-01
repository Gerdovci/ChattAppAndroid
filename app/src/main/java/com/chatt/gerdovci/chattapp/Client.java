package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

public class Client extends AsyncTask<Void, Void, Void> {

    String response = "";
    TextView textResponse;
    EditText myMsg;
    Socket socket = null;

    public Client(TextView textResponse, EditText msg) {
        this.textResponse = textResponse;
        myMsg = msg;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        socket = null;
        try {
            socket = new Socket("192.168.1.67", 8080);
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            // Send first message
            //dOut.writeByte(1);
            String sendText = "Hello pette";
            if (myMsg.getText() != null) {
                sendText = myMsg.getText().toString();
            }

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
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }
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
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}