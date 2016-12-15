package com.chatt.gerdovci.chattapp;

/**
 * Created by Gerdovci on 2016-11-29.
 */

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

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


public class Client extends AsyncTask<Void, Void, Void> {

    String response = "";
    ChatAdapter myChatAdapter;
    EditText myMsg;
    List<SendMessage> messages;
    private LoginRequest myLoginRequest;
    volatile boolean slimUpdate = false;

    public Client(ChatAdapter chatAdapter, EditText msg, LoginRequest loginRequest) {
        myChatAdapter = chatAdapter;
        myMsg = msg;
        messages = new ArrayList<>();
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

        for (; ;) {
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


                SendMessage sendMessage = new SendMessage("0", "uuid", myMsg.getText().toString(), myLoginRequest.getUsername(), date);
                if(myChatAdapter.getCount() != 0){
                    date= myChatAdapter.getMessage(myChatAdapter.getCount()-1).getTimeStamp();
                }


                writeFuture = session.write(sendMessage);


                writeFuture.awaitUninterruptibly();
                UpdateRequest UpdateRequest = new UpdateRequest("0", myLoginRequest.getUsername(), null, null, date);
                session.write(UpdateRequest);
                writeFuture.awaitUninterruptibly();

                read = session.read();
                ReadFuture readFuture = read.awaitUninterruptibly();

                Object message = readFuture.getMessage();
                Log.d("CHATAPP", "always samE?" + message.toString());
                if (message instanceof UpdateResponse) {

                    UpdateResponse message1 = (UpdateResponse) (Object) message;
                    Log.d("CHATAPP", "always samE?www" + message1.getMessages());
                    messages = message1.getMessages();
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
        myMsg.getText().clear();
        super.onPostExecute(result);
    }
}