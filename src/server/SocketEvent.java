package server;

import java.net.Socket;
import java.util.EventObject;

/**
 * Created by emilio on 24/01/16.
 */
public class SocketEvent extends EventObject {
    private String message;
    private int IDclient;
    private String nameClient;
    private StreamSocket streamSocket;

    public SocketEvent(Object e){
        super(e);
    }

    public void setMessage(String stringToServer){
        this.message = stringToServer;
    }

    public void setIDclient(int IDclient){
        this.IDclient = IDclient;
    }

    public int getIDclient(){
        return IDclient;
    }

    public String getMessage(){
        return message;
    }

    public void setNameClient(String nameClient){
        this.nameClient = nameClient;
    }

    public void setStreamSocket(StreamSocket streamSocket){
        this.streamSocket = streamSocket;
    }

    public StreamSocket getStreamSocket(){
        return streamSocket;
    }

    public String getNameClient(){
        return nameClient;
    }
}
