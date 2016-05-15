package server;

import com.mysql.jdbc.Driver;
import kalixdev.info.typing.message.MessageObject;
import server.listener.RequestListener;
import server.listener.StreamSocketListener;
import server.listener.UpdateViewListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by emilio on 16/01/16.
 */
public class Server implements StreamSocketListener {

    private Vector<StreamSocket> arrayStreamSocket = new Vector<StreamSocket>(5);
    private ServerSocket serverSocket;
    private int port;
    private boolean connected = false;

    private RequestListener requestListener;
    private UpdateViewListener updateViewListener;


    public Server(int port){
        this.port = port;
        try {
            //caricamento del driver per la gestione delle connessione con MySql
            //Class.forName("com.mysql.jdbc.Driver");
            new Driver();
        }catch (SQLException e){}
    }

    public void initServer(){
        try {
            serverSocket = new ServerSocket(port);
            setLogView("SERVER IS RUNNING\n");
            connected = true;
            new Thread(new Runnable() {
                public void run() {
                    try{
                        while(connected){
                            Socket socket = serverSocket.accept();
                            StreamSocket temp_socket = new StreamSocket(socket);
                            addListenerToConnection(temp_socket);
                            temp_socket.start();
                        }
                    }catch (Exception ex){setLogView("SERVER STOPPED\n");}
                }
            }).start();
        }catch (Exception e){}

    }

    private void addListenerToConnection(StreamSocket streamSocket){
        streamSocket.addSocketListener(this);
    }

    @Override
    public void onJoinGroupRequest(StreamSocketEvent e) {
        serviceJoinGroupRequest(e);
    }

    private synchronized void serviceJoinGroupRequest(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {
                arrayStreamSocket.add(e.getStreamSocket());
                arrayStreamSocket.lastElement().setIdConnection(arrayStreamSocket.size()-1);
                updateNumOfClients(arrayStreamSocket.size());
                setLogView("ACCEPTED REQUEST FROM "+arrayStreamSocket.lastElement().getSocket().getInetAddress()+'\n');
                notificationNewClient(arrayStreamSocket.lastElement().infoClient());
                MessageObject messageObject = new MessageObject(MessageObject.requestType.ADD_ONLINE_USER);
                messageObject.setUserName(e.getNameClient());
                for(int i=0;i<arrayStreamSocket.size();i++){
                    try {
                        if(!arrayStreamSocket.elementAt(i).getUserClient().equals(e.getNameClient()))
                            arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                    }catch (IOException ex){}

                }
            }
        }).start();
    }

    @Override
    public void onRequestDeleteConnection(StreamSocketEvent e) {
        serviceCommunicationDeleting(e);
    }

    private synchronized void serviceCommunicationDeleting(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {
                setLogView("DISCONNECTION FROM "+e.getNameClient()+'\n');
                notificationRemoveClient(e.getIDclient());
                arrayStreamSocket.removeElementAt(e.getIDclient());
                updateNumOfClients(arrayStreamSocket.size());
                MessageObject messageObject = new MessageObject(MessageObject.requestType.REMOVE_ONLINE_USER);
                messageObject.setUserName(e.getNameClient());
                for(int i=0;i<arrayStreamSocket.size();i++){
                    arrayStreamSocket.elementAt(i).setIdConnection(i);
                    try {
                        arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                    }catch (IOException xe){}

                }
            }
        }).start();
    }

    @Override
    public void onReciveMessage(StreamSocketEvent e) {
        serviceCommunicationMessage(e);
    }

    private synchronized void serviceCommunicationMessage(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {
                setLogView(e.getMessage()+" SEND BY "+
                        arrayStreamSocket.elementAt(e.getIDclient()).getSocket().getInetAddress()+" USERNAME: "+
                        arrayStreamSocket.elementAt(e.getIDclient()).getUserClient().toUpperCase()+'\n');
                for(int i=0;i<arrayStreamSocket.size();i++){
                    if(i != e.getIDclient()){
                        try {
                            MessageObject messageObject = new MessageObject(MessageObject.requestType.SEND_MESSAGE);
                            messageObject.setUserName(e.getNameClient());
                            messageObject.setMessage(e.getMessage());
                            arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                        }catch (Exception ex){}
                    }
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            serverSocket.close();
            connected = false;
            for(int i=0;i<arrayStreamSocket.size();i++){
                arrayStreamSocket.elementAt(i).getOutputStream().writeObject(new MessageObject(MessageObject.requestType.DISCONNECTION_FROM_SERVER));
                arrayStreamSocket.elementAt(i).disconnectionSocket();
            }
            arrayStreamSocket.removeAllElements();
            notificationRemoveAllClients();
            updateNumOfClients(arrayStreamSocket.size());

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void addClientListener(RequestListener requestListener){
        this.requestListener = requestListener;
    }
    public void addUpdateViewListener(UpdateViewListener updateViewListener){this.updateViewListener = updateViewListener;}

    private void notificationNewClient(String[] infoCliet){
        RequestEvent requestEvent = new RequestEvent(this);
        requestEvent.setAddress(infoCliet[0]);
        requestEvent.setName(infoCliet[1]);
        requestListener.onConnectionRequest(requestEvent);
    }

    private void updateNumOfClients(int num){
        UpdateViewEvent e = new UpdateViewEvent(this);
        e.setNumOfClients(num);
        updateViewListener.onRequestUpdateViewClients(e);
    }

    private void setLogView(String log){
        UpdateViewEvent e = new UpdateViewEvent(this);
        e.setLog(log);
        updateViewListener.onRequestUpdateViewLog(e);
    }

    private void notificationRemoveClient(int i){
        RequestEvent requestEvent = new RequestEvent(this);
        requestEvent.setIndexOfclient(i);
        requestListener.onDisconnectionRequest(requestEvent);
    }

    private void notificationRemoveAllClients(){
        RequestEvent requestEvent = new RequestEvent(this);
        requestEvent.removeAllClients();
        requestListener.onDisconnectionRequest(requestEvent);
    }
}
