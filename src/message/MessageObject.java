package message;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by kalix on 3/22/16.
 */
public class MessageObject implements Serializable{
    private Vector<String> list_clients_online = null;
    private String message;
    private String userName = null;
    private String userPsswd;
    private requestType type;

    public enum requestType{
        LOG_IN, SIGN_UP, SEND_MESSAGE, ADD_ONLINE_USER, REMOVE_ONLINE_USER, REQUEST_VALIDATED, REQUEST_NOT_VALIDATED,
        USER_ALREADY_CONNECTED, DISCONNECTION_FROM_CLIENT, DISCONNECTION_FROM_SERVER, REMOVE_ALL_USERS
    }

    public MessageObject(requestType type){
        this.type = type;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUserPsswd(String userPsswd){
        this.userPsswd = userPsswd;
    }
    public void setListOnlineClients(Vector<String> clients_online){
        this.list_clients_online = clients_online;
    }
    public String getUserName(){
        return userName;
    }
    public String getUserPsswd(){
        return userPsswd;
    }
    public String getMessage(){
        return message;
    }
    public requestType getType(){
        return type;
    }
    public Vector<String> getListOnlineClients(){
        return list_clients_online;
    }

}
