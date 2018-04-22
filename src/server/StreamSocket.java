package server;

import kalixdev.info.typing.message.MessageObject;
import server.listener.StreamSocketListener;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;

import static server.TypingDatabase.*;

/**
 * @author emilio acciaro on 16/01/16.
 */

class StreamSocket extends Thread {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String user_client;
    private int ID_connection;
    private static ArrayList<String> clientsConnected = new ArrayList<>(5);
    private Connection connection;
    private Statement statement;
    private StreamSocketListener streamSocketListener;
    private boolean connected = false;

    /**
     * Default constructor to make an asynchronous communication whit client
     * @param socket to make a connection with client
     */
    StreamSocket(Socket socket){
        this.socket = socket;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method validate the credential sent by user client
     * @return true if the request is validated, else false
     * @throws Exception
     * @see StreamSocket#doLogin(String, String)
     * @see StreamSocket#doSignup(String, String)
     */
    private boolean validateRequestType()throws Exception{

        boolean request = false;
        MessageObject messageObject = (MessageObject) inputStream.readObject();
        user_client = messageObject.getUserName();
        switch (messageObject.getType()){
            case LOG_IN:
                request = doLogin(messageObject.getUserName(),messageObject.getUserPassword());
                break;
            case SIGN_UP:
                request = doSignup(messageObject.getUserName(),messageObject.getUserPassword());
                break;
        }
        return request;
    }


    public void run(){
        try {
            if(!clientsConnected.contains(user_client)){
                if(validateRequestType()){
                    sendRequestGroupAdd();
                    addClient();
                    MessageObject messageObject = new MessageObject(MessageObject.RequestType.REQUEST_VALIDATED);
                    messageObject.setMessage("Benvenuto nella chat "+user_client+"!");
                    messageObject.setListOnlineClients(clientsConnected);
                    outputStream.writeObject(messageObject);
                    connected = true;
                    while(connected){
                        MessageObject messageObject1 = (MessageObject)inputStream.readObject();
                        if(messageObject1.getType().equals(MessageObject.RequestType.DISCONNECTION_FROM_CLIENT)){
                            sendDisconnectionRequest();
                            disconnectionSocket();
                            break;
                        }
                        else if(!messageObject1.getMessage().isEmpty() && !messageObject1.getMessage().equals(" ")){
                            sendMessageToServer(messageObject1.getMessage());
                        }
                    }
                }else{
                    outputStream.writeObject(new MessageObject(MessageObject.RequestType.REQUEST_NOT_VALIDATED));
                }
            }else {
                outputStream.writeObject(new MessageObject(MessageObject.RequestType.USER_ALREADY_CONNECTED));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addSocketListener(StreamSocketListener streamSocketListener){
        this.streamSocketListener = streamSocketListener;
    }

    private void sendRequestGroupAdd(){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setStreamSocket(this);
        streamSocketEvent.setNameClient(user_client);
        streamSocketListener.onJoinGroupRequest(streamSocketEvent);
    }

    private void sendMessageToServer(String s){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setNameClient(user_client);
        streamSocketEvent.setMessage(s);
        streamSocketEvent.setIdClient(ID_connection);
        streamSocketListener.onReceiveMessage(streamSocketEvent);
    }

    private void sendDisconnectionRequest(){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setIdClient(ID_connection);
        streamSocketEvent.setNameClient(user_client);
        streamSocketListener.onRequestDeleteConnection(streamSocketEvent);
    }

    String[] infoClient(){
        return new String[]{socket.getInetAddress().toString(),user_client};
    }

    /**
     * This method try to make a login
     * Connect to typing database {@link TypingDatabase}
     * Do a SELECT with specific credentials
     * At the end of the process make a disconnection from DB
     *
     * @param nomeUtente first credential to make a login request
     * @param psswd second credential to make a signup request
     * @return result, true if credential is correct else false
     */
    private boolean doLogin(String nomeUtente, String psswd){
        boolean result = false;
        ResultSet resultSet;
        String cripted_psswd = cryptPassword(psswd);
        String user_found = "";
        String password_found = "";
        connectToDB();
        try {
            resultSet = statement.executeQuery("SELECT * FROM users WHERE u_name='"+nomeUtente
                    +"' AND u_psswd='"+cripted_psswd+"'");
            while (resultSet.next()){
                user_found = resultSet.getString(2);
                password_found = resultSet.getString(3);
            }
            if(user_found.equals(nomeUtente) && password_found.equals(cripted_psswd)){
                result = true;
                resultSet.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnectFromDB();
        }
        return result;
    }

    /**
     * This method try to make a signup
     * Connect to typing database {@link TypingDatabase}
     * Do a SELECT with specific credentials
     * At the end of the process make a disconnection from DB
     *
     * @param nomeUtente first credential to make a login request
     * @param psswd second credential to make a signup request
     * @return result, true if username not exist else false
     */
    private boolean doSignup(String nomeUtente, String psswd){
        boolean val_res = false;
        String cryptedPassword = cryptPassword(psswd);
        connectToDB();
        try {
            int response = statement.executeUpdate("INSERT INTO users(u_name, u_psswd) VALUES('"+nomeUtente
                    +"', '"+cryptedPassword+"')");
            if(response>0){
                val_res = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnectFromDB();
        }
        return val_res;
    }

    /**
     * Create a connection with {@link TypingDatabase}
     */
    private void connectToDB(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                    + ADDRESS
                    +":3306/"
                    + DB_NAME
                    +"", ADMIN_NAME, ADMIN_PASSWORD);
            statement = connection.createStatement();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Make a disconnection to {@link TypingDatabase}
     */
    private void disconnectFromDB(){
        try {
            statement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * This method allow to crypting a string
     * whit SHA-256 hash function
     *
     * @param password string credential gets from client
     * @return hashString, crypt password
     * @see MessageDigest
     */
    private String cryptPassword(String password){
        String hashString = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            hashString = new BigInteger(1, crypt.digest()).toString(16);
        }catch (Exception e){
            e.printStackTrace();
        }
        return hashString;
    }

    /**
     * To make total disconnection from client
     */
    void disconnectionSocket(){
        removeClient();
        connected = false;
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private synchronized void removeClient(){
        clientsConnected.remove(user_client);
    }
    private synchronized void addClient(){
        clientsConnected.add(user_client);
    }

    Socket getSocket(){
        return socket;
    }
    ObjectOutputStream getOutputStream(){
        return outputStream;
    }
    String getUserClient(){
        return user_client;
    }
    void setIdConnection(int id){
        ID_connection = id;
    }

}
