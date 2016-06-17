package server;

import java.util.EventObject;

/**
 * @author emilio acciaro on 24/01/16.
 */
public class RequestEvent extends EventObject{
    private static final long serialVersionUID = 1L;

    private String address;
    private String name;
    private int indexOfClient;
    boolean removeAllClients = false;

    RequestEvent(Object e){
        super(e);
    }
    void setAddress(String address){
        this.address = address;
    }
    void setName(String name){
        this.name = name;
    }

    void setIndexOfClient(int i){
        this.indexOfClient = i;
    }

    String[] getClientValues(){
        return new String[]{address,name};
    }

    int getIndexOfClient(){
        return indexOfClient;
    }

    void removeAllClients(){ removeAllClients = true;}
}

