package server;

import kalixdev.info.typing.message.MessageObject;
import server.listener.StreamSocketListener;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Vector;

/**
 * Created by emilio on 16/01/16.
 */
public class StreamSocket extends Thread {
    private Socket sct;
    private ObjectOutputStream outputStream;
    private ObjectInputStream bufferedReader;
    private String user_client;
    private int ID_connection;
    private static Vector<String> name_of_users_connected = new Vector<>(5);
    private Connection connection = null;
    private Statement statement = null;
    private StreamSocketListener streamSocketListener;
    private boolean connected = false;

    public StreamSocket(Socket sct){
        this.sct = sct;
        try {
            outputStream = new ObjectOutputStream(sct.getOutputStream());
            bufferedReader = new ObjectInputStream(sct.getInputStream());
        }catch (Exception e){}
    }

    public Socket getSocket(){
        return sct;
    }
    public ObjectOutputStream getOutputStream(){
        return outputStream;
    }
    public ObjectInputStream getBufferedReader(){
        return bufferedReader;
    }
    public String getUserClient(){
        return user_client;
    }
    public int getIdConnection(){
        return ID_connection;
    }
    public void setIdConnection(int id){
        ID_connection = id;
    }


    public boolean validateRequestType()throws Exception{
        boolean request = false;
        MessageObject messageObject = (MessageObject) bufferedReader.readObject();
        user_client = messageObject.getUserName();
        switch (messageObject.getType()){
            case LOG_IN:
                request = sign_in(messageObject.getUserName(),messageObject.getUserPsswd());
                break;
            case SIGN_UP:
                request = sign_up(messageObject.getUserName(),messageObject.getUserPsswd());
                break;
        }
        return request;
    }

    public void run(){
        try {
            if(validateRequestType()){
                if(!isOnListOfClientsConnected(user_client)){
                    sendRequestGroupAdd();
                    addClient();
                    MessageObject messageObject = new MessageObject(MessageObject.requestType.REQUEST_VALIDATED);
                    messageObject.setMessage("Benvenuto nella chat "+user_client+"!");
                    messageObject.setListOnlineClients(name_of_users_connected);
                    outputStream.writeObject(messageObject);
                    connected = true;
                    while(connected){
                        MessageObject messageObject1 = (MessageObject)bufferedReader.readObject();
                        if(messageObject1.getType().equals(MessageObject.requestType.DISCONNECTION_FROM_CLIENT)){
                            sendDisconnectionRequest();
                            disconnectionSocket();
                            break;
                        }
                        else if(!messageObject1.getMessage().isEmpty() && !messageObject1.getMessage().equals(" ")){
                            sendMessageToServer(messageObject1.getMessage());
                        }
                    }
                }else{
                    outputStream.writeObject(new MessageObject(MessageObject.requestType.USER_ALREADY_CONNECTED));
                }
            }else {
                outputStream.writeObject(new MessageObject(MessageObject.requestType.REQUEST_NOT_VALIDATED));
            }
        }catch (Exception e){}
    }

    public void addSocketListener(StreamSocketListener streamSocketListener){
        this.streamSocketListener = streamSocketListener;
    }

    public void sendRequestGroupAdd(){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setStreamSocket(this);
        streamSocketEvent.setNameClient(user_client);
        streamSocketListener.onJoinGroupRequest(streamSocketEvent);
    }

    private void sendMessageToServer(String s){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setNameClient(user_client);
        streamSocketEvent.setMessage(s);
        streamSocketEvent.setIDclient(ID_connection);
        streamSocketListener.onReciveMessage(streamSocketEvent);
    }

    private void sendDisconnectionRequest(){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setIDclient(ID_connection);
        streamSocketEvent.setNameClient(user_client);
        streamSocketListener.onRequestDeleteConnection(streamSocketEvent);
    }

    public String[] infoClient(){
        return new String[]{sct.getInetAddress().toString(),user_client};
    }

    //metodo di log-in
    private boolean sign_in(String nomeUtente, String psswd){
        boolean val_res = false;
        ResultSet resultSet = null;
        String cripted_psswd = cryptPsswd(psswd);
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
                val_res = true;
                resultSet.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnectFromDB();
        }
        return val_res;
    }

    //metodo di registrazione
    private boolean sign_up(String nomeUtente, String psswd){
        boolean val_res = false;
        String cripted_psswd = cryptPsswd(psswd);
        connectToDB();
        try {
            int r = statement.executeUpdate("INSERT INTO users(u_name, u_psswd) VALUES('"+nomeUtente
                    +"', '"+cripted_psswd+"')");
            if(r>0){
                val_res = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnectFromDB();
        }
        return val_res;
    }

    private void connectToDB(){

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_system","root","toor");
            statement = connection.createStatement();
        }catch (SQLException e){}

    }

    private void disconnectFromDB(){
        try {
            statement.close();
            connection.close();
        }catch (SQLException e){}
    }

    //criptazione della password con la funzione di hash SHA-256 (versione 2 della funzione SHA)
    private String cryptPsswd(String psswd){
        String hashString = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(psswd.getBytes("UTF-8"));
            hashString = new BigInteger(1, crypt.digest()).toString(16);
        }catch (Exception e){}
        return hashString;
    }

    private synchronized boolean isOnListOfClientsConnected(String user_client){
        boolean res = false;
        for(int i=0;i<name_of_users_connected.size();i++){
            if(name_of_users_connected.elementAt(i).equals(user_client)){
                res = true;
            }
        }
        return res;
    }

    public void disconnectionSocket(){
        removeClient();
        connected = false;
        try {
            outputStream.close();
            bufferedReader.close();
            sct.close();
        }catch (IOException e){}

    }

    private synchronized void removeClient(){
        name_of_users_connected.removeElement(user_client);
    }
    private synchronized void addClient(){
        name_of_users_connected.add(user_client);
    }

}
