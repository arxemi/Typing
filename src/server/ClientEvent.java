package server;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by emilio on 24/01/16.
 */
public class ClientEvent extends EventObject{
    private static final long serialVersionUID = 1L;
    private String address;
    private String name;
    private int indexOfclient;
    boolean removeAllClients_ = false;

    public ClientEvent(Object e){
        super(e);
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setIndexOfclient(int i){
        this.indexOfclient = i;
    }

    public String[] getClientValues(){
        return new String[]{address,name};
    }

    public int getIndexOfclient(){
        return indexOfclient;
    }

    public void removeAllClients(){ removeAllClients_ = true;}
}

