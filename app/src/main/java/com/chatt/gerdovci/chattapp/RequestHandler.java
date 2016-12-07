package com.chatt.gerdovci.chattapp;

import com.pette.server.common.LoginRequest;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gerdovci on 2016-12-07.
 */

public class RequestHandler extends IoHandlerAdapter {

    private boolean finished;
    private Object myObject;

    public List<Object> getResult() {
        return result;
    }

    private List<Object> result;

    public RequestHandler(Object object) {
        myObject= object;
        result = new ArrayList<>();
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void sessionOpened(IoSession session) {
        // send summation requests
       // session.write(myObject);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if(message instanceof List) {
            result = (List<Object>) message;
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.closeNow();
    }
}
