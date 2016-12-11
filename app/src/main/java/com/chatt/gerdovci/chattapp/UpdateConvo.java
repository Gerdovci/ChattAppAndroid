package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */


import android.os.AsyncTask;
import android.util.Log;

import com.pette.server.common.LoginRequest;
import com.pette.server.common.SendMessage;
import com.pette.server.common.UpdateRequest;
import com.pette.server.common.UpdateResponse;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpdateConvo extends AsyncTask<Void, Void, Void> {

    String response = "";
    ChatAdapter myChatAdapter;
    List<SendMessage> messages = new ArrayList<>();
    LoginRequest myLoginRequest;
    volatile boolean slimUpdate = false;

    public UpdateConvo(ChatAdapter chatAdapter, LoginRequest loginRequest) {
        myChatAdapter = chatAdapter;
        myLoginRequest = loginRequest;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(500);

        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        RequestHandler requestHandler = new RequestHandler(new IoHandlerAdapter());
        connector.setHandler(new IoHandlerAdapter());
        IoSession session = null;


        for (; ; ) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(ItemDetailActivity.IP_ADDRESS, ItemDetailActivity.SERVER_PORT));
                future.awaitUninterruptibly();
                session = future.getSession();
                session.getConfig().setUseReadOperation(true);

                WriteFuture writeFuture = session.write(myLoginRequest);
                writeFuture.awaitUninterruptibly();

                ReadFuture read = session.read();
                read.awaitUninterruptibly();

                Date date = new Date();
                if (myChatAdapter.getCount() != 0) {
                    SendMessage message = myChatAdapter.getMessage(myChatAdapter.getCount() - 1);
                    Log.d("CHATAP", message.getMessageBody() + "  " + message.getTimeStamp());
                    date = myChatAdapter.getMessage(myChatAdapter.getCount() - 1).getTimeStamp();
                    slimUpdate = true;

                } else {
                    date = null;
                }


                UpdateRequest UpdateRequest = new UpdateRequest("0", myLoginRequest.getUsername(), null, null, date);
                session.write(UpdateRequest);
                writeFuture.awaitUninterruptibly();

                read = session.read();
                ReadFuture readFuture = read.awaitUninterruptibly();

                Object message = readFuture.getMessage();
                if (message instanceof UpdateResponse) {

                    UpdateResponse message1 = (UpdateResponse) (Object) message;
                    messages = message1.getMessages();


                    for (SendMessage sendMessage : messages) {

                        Log.d("CHATAPP", "always samE?www" + sendMessage.getMessageBody());
                    }
                }

                System.err.println("jaha");
                // wait until the summation is done
                session.closeNow();
                connector.dispose();
                break;
            } catch (RuntimeIoException e) {

                System.err.println("ErrorBro");
                e.printStackTrace();
                break;
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        ItemDetailActivity.sendTextMessage(messages, slimUpdate);
        super.onPostExecute(result);
    }
}