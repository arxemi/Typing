package server;

import java.util.EventObject;

/**
 * @author emilio acciaro on 24/01/16.
 */
public class StreamSocketEvent extends EventObject {
    private String message;
    private int idClient;
    private String nameClient;
    private StreamSocket streamSocket;

    public StreamSocketEvent(Object e){
        super(e);
    }

    public void setMessage(String stringToServer){
        this.message = stringToServer;
    }

    public void setIdClient(int idClient){
        this.idClient = idClient;
    }

    public int getIdClient(){
        return idClient;
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
