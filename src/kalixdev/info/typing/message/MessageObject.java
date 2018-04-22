package kalixdev.info.typing.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author emilio acciaro on 3/22/16.
 */

public class MessageObject implements Serializable{

    static final long serialVersionUID = 123456789123456789L;

    private ArrayList<String> onlineClients;
    private String message;
    private String userName;
    private String userPassword;
    private RequestType type;

    public enum RequestType{
        LOG_IN, SIGN_UP, SEND_MESSAGE, ADD_ONLINE_USER, REMOVE_ONLINE_USER, REQUEST_VALIDATED, REQUEST_NOT_VALIDATED,
        USER_ALREADY_CONNECTED, DISCONNECTION_FROM_CLIENT, DISCONNECTION_FROM_SERVER, REMOVE_ALL_USERS
    }

    public MessageObject(RequestType type){
        this.type = type;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setUserPassword(String userPassword){
        this.userPassword = userPassword;
    }

    public void setListOnlineClients(ArrayList<String> clients_online){
        this.onlineClients = clients_online;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public String getMessage(){
        return message;
    }

    public RequestType getType(){
        return type;
    }

    public ArrayList<String> getListOnlineClients(){
        return onlineClients;
    }

}
