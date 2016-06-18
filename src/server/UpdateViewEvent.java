package server;

import java.util.EventObject;

/**
 * @author emilio acciaro on 26/01/16.
 */
public class UpdateViewEvent extends EventObject {
    private int numOfClients;
    private String log;

    UpdateViewEvent(Object e){
        super(e);
    }

    void setLog(String log){
        this.log = log;
    }

    String getLog(){
        return log;
    }

    void setNumOfClients(int numOfClients){
        this.numOfClients = numOfClients;
    }

    int getNumOfClients(){
        return numOfClients;
    }
}
