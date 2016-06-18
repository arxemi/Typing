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

    StreamSocketEvent(Object e){
        super(e);
    }

    void setMessage(String stringToServer){
        this.message = stringToServer;
    }

    void setIdClient(int idClient){
        this.idClient = idClient;
    }

    int getIdClient(){
        return idClient;
    }

    String getMessage(){
        return message;
    }

    void setNameClient(String nameClient){
        this.nameClient = nameClient;
    }

    void setStreamSocket(StreamSocket streamSocket){
        this.streamSocket = streamSocket;
    }

    StreamSocket getStreamSocket(){
        return streamSocket;
    }

    String getNameClient(){
        return nameClient;
    }
}
