package client;

import java.util.EventObject;
import java.util.Vector;

/**
 * Created by kalix on 3/25/16.
 */
public class ClientServiceEvent extends EventObject {
    private String message;
    // nome client da aggiornare nella JList
    private String nameClient;
    private Vector<String> clientsOnline;
    public ClientServiceEvent(Object e){
        super(e);
    }
    public void setMessage(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }
    public void setNameClient(String nameClient){
        this.nameClient = nameClient;
    }
    public String getNameClient(){
        return nameClient;
    }

    public void setClientsOnline(Vector<String> clientsOnline) {
        this.clientsOnline = clientsOnline;
    }

    public Vector<String> getClientsOnline() {
        return clientsOnline;
    }
}
