package server;

import server.listener_interface.RequestListener;
import server.listener_interface.StreamSocketListener;
import server.listener_interface.UpdateViewListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by emilio on 16/01/16.
 */
public class Server implements StreamSocketListener {

    private Vector<StreamSocket> arrayStreamSocket = new Vector<StreamSocket>(5);
    private ServerSocket serverSocket;
    private Socket socket;

    private int port;
    public static final String CLOSING_CODE = "hsh53wh2k2b38dnwy3nu3tdb38bd7eyf2debidg2\n";

    RequestListener requestListener;
    UpdateViewListener updateViewListener;


    public Server(int port){
        this.port = port;
        try {
            //caricamento del driver per la gestione delle connessione con MySql
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e){}
    }

    public void initServer(){
        try {
            serverSocket = new ServerSocket(port);
            setLogView("Server partito in esecuzione\n");
            while(true){
                socket = serverSocket.accept();
                StreamSocket temp_socket = new StreamSocket(socket);
                temp_socket.addSocketListener(this);
                temp_socket.start();
            }

        }catch (Exception e){}
        setLogView("Fine esecuzione server\n");
    }

    @Override
    public void onJoinGroupRequest(StreamSocketEvent e) {
        arrayStreamSocket.add(e.getStreamSocket());
        arrayStreamSocket.lastElement().ID = arrayStreamSocket.size()-1;
        updateNumOfClients(arrayStreamSocket.size());
        setLogView("New request from "+arrayStreamSocket.lastElement().sct.getInetAddress()+'\n');
        notificationNewClient(arrayStreamSocket.lastElement().infoClient());
    }

    @Override
    public void onRequestDeleteConnection(StreamSocketEvent e) {
        setLogView("RIMOSSO client "+e.getNameClient()+'\n');
        notificationRemoveClient(e.getIDclient());
        arrayStreamSocket.removeElementAt(e.getIDclient());
        updateNumOfClients(arrayStreamSocket.size());
        for(int i=0;i<arrayStreamSocket.size();i++){ arrayStreamSocket.elementAt(i).ID = i;}
    }

    @Override
    public void onReciveMessage(StreamSocketEvent e) {
        setLogView(e.getMessage()+" INVIATO DA "+
                arrayStreamSocket.elementAt(e.getIDclient()).sct.getInetAddress()+" NOME: "+
                arrayStreamSocket.elementAt(e.getIDclient()).user_client.toUpperCase()+'\n');
        for(int i=0;i<arrayStreamSocket.size();i++){
            if(i != e.getIDclient()){
                try {
                    arrayStreamSocket.elementAt(i).outputStream.writeBytes(e.getNameClient().toUpperCase()+ ": "+
                            e.getMessage()+'\n');
                }catch (Exception ex){}
            }
        }
    }

    public void disconnect(){
        try {
            serverSocket.close();
            for(int i=0;i<arrayStreamSocket.size();i++){
                arrayStreamSocket.elementAt(i).outputStream.writeBytes(CLOSING_CODE);
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
