package server;

import java.util.EventObject;

/**
 * @author emilio acciaro on 26/01/16.
 */
public class UpdateViewEvent extends EventObject {
    private int numOfClients;
    private String log;

    public UpdateViewEvent(Object e){
        super(e);
    }

    public void setLog(String log){
        this.log = log;
    }

    public String getLog(){
        return log;
    }

    public void setNumOfClients(int numOfClients){
        this.numOfClients = numOfClients;
    }

    public int getNumOfClients(){
        return numOfClients;
    }
}
