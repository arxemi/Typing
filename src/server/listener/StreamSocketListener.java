package server.listener;

import server.StreamSocketEvent;

import java.util.EventListener;

/**
 * @author emilio acciaro on 24/01/16.
 */
public interface StreamSocketListener extends EventListener{
    void onReceiveMessage(StreamSocketEvent e);
    void onRequestDeleteConnection(StreamSocketEvent e);
    void onJoinGroupRequest(StreamSocketEvent e);
}
