package client;

import kalixdev.info.typing.message.MessageObject;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by emilio on 16/01/16.
 */
public class Client {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream bufferedReader;
    private ClientServiceListener clientServiceListener;
    private boolean connected = true;


    public Client(String indirizzo, int port, String nomeClient, String psswd, MessageObject.requestType type)throws Exception{
        MessageObject messageObject = new MessageObject(type);
        messageObject.setUserName(nomeClient);
        messageObject.setUserPsswd(psswd);
        socket = new Socket(indirizzo,port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        bufferedReader = new ObjectInputStream(socket.getInputStream());
        outputStream.writeObject(messageObject);

    }

    public void setClientServiceListener(ClientServiceListener clientServiceListener){
        this.clientServiceListener = clientServiceListener;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void startClientService(){
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    while(connected){
                        MessageObject objectMessage = (MessageObject)bufferedReader.readObject();
                        switch (objectMessage.getType()){
                            case DISCONNECTION_FROM_SERVER:
                                sendMessageToWindowClient("COMMUNICATION SERVICE: server unreachable!");
                                updateNameOfClientsOnline(MessageObject.requestType.REMOVE_ALL_USERS);
                                socket.close();
                                connected = false;
                                break;
                            case SEND_MESSAGE:
                                sendMessageToWindowClient(objectMessage.getUserName()+": "+objectMessage.getMessage());
                                break;
                            case ADD_ONLINE_USER:
                                updateNameOfClientsOnline(MessageObject.requestType.ADD_ONLINE_USER,objectMessage.getUserName());
                                sendMessageToWindowClient(objectMessage.getUserName()+" joined to chat!");
                                break;
                            case REMOVE_ONLINE_USER:
                                updateNameOfClientsOnline(MessageObject.requestType.REMOVE_ONLINE_USER,objectMessage.getUserName());
                                sendMessageToWindowClient(objectMessage.getUserName()+" left from chat!");
                                break;
                        }
                    }

                }catch (Exception e){}
            }
        });
        t.start();
    }

    public void sendMessageToWindowClient(String s){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setMessage(s);
        clientServiceListener.onReciveMessageFromServer(cse);
    }

    public void updateNameOfClientsOnline(MessageObject.requestType type){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        if(type.equals(MessageObject.requestType.REMOVE_ALL_USERS)){
            clientServiceListener.removeClientFromList(cse);
        }
    }

    public void updateNameOfClientsOnline(MessageObject.requestType type, String name){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setNameClient(name);
        if(type.equals(MessageObject.requestType.ADD_ONLINE_USER)){
            clientServiceListener.addClientToList(cse);
        }else if(type.equals(MessageObject.requestType.REMOVE_ONLINE_USER)){
            clientServiceListener.removeClientFromList(cse);
        }
    }
    public void updateNameOfClientsOnline(MessageObject.requestType type, Vector<String> names){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setClientsOnline(names);
        if(type.equals(MessageObject.requestType.ADD_ONLINE_USER)){
            clientServiceListener.addClientToList(cse);

        }else if(type.equals(MessageObject.requestType.REMOVE_ONLINE_USER)){
            clientServiceListener.removeClientFromList(cse);
        }
    }


    public MessageObject getResponseFromServer(){
        MessageObject messageObject = null;
        try {
            messageObject = (MessageObject) bufferedReader.readObject();
        }catch (Exception e){}
        return  messageObject;
    }

    public void disconnect(MessageObject.requestType type){
        if(!socket.isClosed()){
            try {
                outputStream.writeObject(new MessageObject(type));
                connected = false;
                bufferedReader.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}