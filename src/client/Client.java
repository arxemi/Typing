package client;

import kalixdev.info.typing.message.MessageObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * @author acciaro emilio on 16/01/16.
 */

class Client {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ClientServiceListener clientServiceListener;
    private boolean connected = true;

    /**
     * Default constructor to make a connection with server
     *
     * @param address need to make a {@link Socket} object
     * @param port need to make a {@link Socket} object
     * @param nomeClient  specific username to connect with server
     * @param password associated password to specific username
     * @param type allow to make different type of request
     * @see MessageObject
     */
    Client(String address, int port, String nomeClient, String password, MessageObject.RequestType type)throws Exception{
        MessageObject messageObject = new MessageObject(type);
        messageObject.setUserName(nomeClient);
        messageObject.setUserPassword(password);
        socket = new Socket(address,port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream.writeObject(messageObject);
    }

    /**
     * This method allow to link this class to {@link WindowClient}
     * and to interact with methods of {@link ClientServiceListener}
     *
     * @param  clientServiceListener to attach listener
     * @see    ClientServiceListener
     */
    void setClientServiceListener(ClientServiceListener clientServiceListener){
        this.clientServiceListener = clientServiceListener;
    }


    /**
     * Returns an {@link ObjectOutputStream} object that allow to write on socket communication.
     *
     * @return      the specific private object
     * @see         ObjectOutputStream
     */
    ObjectOutputStream getOutputStream() {
        return outputStream;
    }


    /**
     * This method make a thread that create an asynchronous communication with the socket of server
     *
     * @see MessageObject
     * @see Thread
     */
    void startClientService(){
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(connected){
                    MessageObject objectMessage = getResponseFromServer();
                    switch (objectMessage.getType()){
                        case DISCONNECTION_FROM_SERVER:
                            sendCriticalMessageToWindowClient("COMMUNICATION SERVICE: server unreachable!");
                            updateNameOfClientsOnline(MessageObject.RequestType.REMOVE_ALL_USERS);
                            closeConnection();
                            connected = false;
                            break;
                        case SEND_MESSAGE:
                            sendMessageToWindowClient(objectMessage.getUserName().toUpperCase()+": "+objectMessage.getMessage());
                            break;
                        case ADD_ONLINE_USER:
                            updateNameOfClientsOnline(MessageObject.RequestType.ADD_ONLINE_USER,objectMessage.getUserName());
                            sendMessageToWindowClient(objectMessage.getUserName().toUpperCase()+" joined to chat!");
                            break;
                        case REMOVE_ONLINE_USER:
                            updateNameOfClientsOnline(MessageObject.RequestType.REMOVE_ONLINE_USER,objectMessage.getUserName());
                            sendMessageToWindowClient(objectMessage.getUserName().toUpperCase()+" left from chat!");
                            break;
                    }
                }
            }
        });
        t.start();
    }

    /**
     * This method close all buffers and close the socket connection
     * @see Socket
     * @see InputStream
     * @see OutputStream
     */

    private void closeConnection(){
        try {
            socket.close();
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessageToWindowClient(String s){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setMessage(s);
        clientServiceListener.onReceiveMessageFromServer(cse);
    }

    private void sendCriticalMessageToWindowClient(String s){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setMessage(s);
        clientServiceListener.onReceiveCriticalMessageFromServer(cse);
    }

    private void updateNameOfClientsOnline(MessageObject.RequestType type){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        if(type.equals(MessageObject.RequestType.REMOVE_ALL_USERS)){
            clientServiceListener.removeClientFromList(cse);
        }
    }

    private void updateNameOfClientsOnline(MessageObject.RequestType type, String name){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setNameClient(name);
        if(type.equals(MessageObject.RequestType.ADD_ONLINE_USER)){
            clientServiceListener.addClientToList(cse);
        }else if(type.equals(MessageObject.RequestType.REMOVE_ONLINE_USER)){
            clientServiceListener.removeClientFromList(cse);
        }
    }

    void updateNameOfClientsOnline(MessageObject.RequestType type, ArrayList<String> names){
        ClientServiceEvent cse = new ClientServiceEvent(this);
        cse.setClientsOnline(names);
        if(type.equals(MessageObject.RequestType.ADD_ONLINE_USER)){
            clientServiceListener.addClientToList(cse);

        }else if(type.equals(MessageObject.RequestType.REMOVE_ONLINE_USER)){
            clientServiceListener.removeClientFromList(cse);
        }
    }

    /**
     * This method return a {@link MessageObject} object read by inputStream
     *
     * @return messageObject
     * @see MessageObject
     */
    MessageObject getResponseFromServer(){
        MessageObject messageObject = null;
        try {
            messageObject = (MessageObject) inputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  messageObject;
    }

    /**
     * This method allow to disconnect from {@link WindowClient} and
     * to send a specific request before disconnecting
     *
     * @param type to specify the method of disconnection
     * @see kalixdev.info.typing.message.MessageObject.RequestType
     */
    void disconnect(MessageObject.RequestType type){
        if(!socket.isClosed()){
            try {
                outputStream.writeObject(new MessageObject(type));
                closeConnection();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}