package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UpdateConvo extends AsyncTask<Void, Void, Void> {

    String response = "";
    TextView textResponse;

    public UpdateConvo(TextView textResponse) {
        this.textResponse = textResponse;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;
        try {
            socket = new Socket("192.168.1.67", 8080);
            Log.d("PETTE", "in here");


            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            // Send first message
            //dOut.writeByte(1);
            String sendText = "GET UPDATECONVO";

            dOut.writeUTF(sendText);
            dOut.flush(); // Send off the data


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();


            if (inputStream.available() == -1) {
                socket.close();
                Log.d("PETTE", "closing down socket");
                return null;
            }

            Log.d("PETTE", "Will read stream now");
          /*
          * notice: inputStream.read() will block if no data return
          */
            socket.setSoTimeout(1000);
            Log.d("PETTE", "Reading");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                Log.d("PETTE", response);
                break;
            }
            inputStream.close();

        } catch (SocketTimeoutException e) {
            Log.d("PETTE", e.toString());
        }catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }  finally {
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
        if (response.length() != 0) {
            textResponse.setText(response);
        }
        super.onPostExecute(result);
    }

}