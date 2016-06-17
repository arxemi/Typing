package client;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author emilio acciaro on 3/25/16.
 */

class ClientServiceEvent extends EventObject {
    private String message;
    private String nameClient;
    private ArrayList<String> clientsOnline;

    ClientServiceEvent(Object e){
        super(e);
    }

    void setMessage(String message){
        this.message=message;
    }

    String getMessage(){
        return message;
    }

    void setNameClient(String nameClient){
        this.nameClient = nameClient;
    }

    String getNameClient(){
        return nameClient;
    }

    void setClientsOnline(ArrayList<String> clientsOnline) {
        this.clientsOnline = clientsOnline;
    }

    ArrayList<String> getClientsOnline() {
        return clientsOnline;
    }
}
