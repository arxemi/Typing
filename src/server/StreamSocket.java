package server;

import server.listener_interface.StreamSocketListener;

import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private DataOutputStream outputStream;
    private BufferedReader bufferedReader;
    private String user_client;
    private int ID_connection;
    static Vector<String> name_of_users_connected = new Vector<>(5);
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;



    StreamSocketListener streamSocketListener;

    public StreamSocket(Socket sct){
        this.sct = sct;
        try {
            outputStream = new DataOutputStream(sct.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(sct.getInputStream()));
        }catch (Exception e){}
    }

    public Socket getSocket(){
        return sct;
    }
    public DataOutputStream getOutputStream(){
        return outputStream;
    }
    public BufferedReader getBufferedReader(){
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
        final int LOG_IN_REQUEST = 0;
        final int SIGN_UP_REQUEST = 1;
        //gestire tipo richiesta
        int request_type = Integer.parseInt(bufferedReader.readLine());
        user_client = bufferedReader.readLine();
        String psswd = bufferedReader.readLine();
        boolean request = false;

        if(request_type == LOG_IN_REQUEST){
            request = sign_in(user_client,psswd);
        }else if(request_type == SIGN_UP_REQUEST){
            request = sign_up(user_client,psswd);
        }
        return request;
    }

    public void run(){
        final String REQUEST_VALIDATED = "501-fh4hjh45h4";
        final String REQUEST_NOT_VALIDATED = "500-fh4hjh45h4";
        final String USER_ALREADY_CONNECTED = "502-fh4hjh45h4";
        try {
            if(validateRequestType()){
                if(!isOnListOfClientsConnected(user_client)){
                    sendRequestGroupAdd();
                    name_of_users_connected.add(user_client);
                    outputStream.writeBytes(REQUEST_VALIDATED+"\n");
                    outputStream.writeBytes("Benvenuto nella chat "+user_client+"!\n");
                    while(true){
                        String stringa = bufferedReader.readLine();
                        if(stringa.equals("b5ih45i54g5hk443hbk43b")){
                            sendDisconnectionRequest();
                            break;
                        }
                        else if(stringa.equals("hsh53wh2k2b38dnwy3nu3tdb38bd7eyf2debidg2")){
                            break;
                        }
                        else if(!stringa.isEmpty() && !stringa.equals(" ")){
                            sendMessageToServer(stringa);
                        }
                    }
                    name_of_users_connected.removeElement(user_client);
                    outputStream.close();
                    bufferedReader.close();
                    sct.close();
                }else{
                    outputStream.writeBytes(USER_ALREADY_CONNECTED+"\n");
                }
            }else {
                outputStream.writeBytes(REQUEST_NOT_VALIDATED+"\n");
            }
        }catch (Exception e){}
    }

    public void addSocketListener(StreamSocketListener streamSocketListener){
        this.streamSocketListener = streamSocketListener;
    }

    public void sendRequestGroupAdd(){
        StreamSocketEvent streamSocketEvent = new StreamSocketEvent(this);
        streamSocketEvent.setStreamSocket(this);
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

    private boolean isOnListOfClientsConnected(String user_client){
        boolean res = false;
        for(int i=0;i<name_of_users_connected.size();i++){
            if(name_of_users_connected.elementAt(i).equals(user_client)){
                res = true;
            }
        }
        return res;
    }

}
