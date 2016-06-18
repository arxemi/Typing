package server;

import com.mysql.jdbc.Driver;
import kalixdev.info.typing.message.MessageObject;
import server.listener.RequestListener;
import server.listener.StreamSocketListener;
import server.listener.UpdateViewListener;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


/**
 * @author emilio acciaro on 16/01/16.
 */

class Server implements StreamSocketListener {

    private Vector<StreamSocket> arrayStreamSocket = new Vector<>(5);
    private ServerSocket serverSocket;
    private int port;
    private boolean connected = false;

    private RequestListener requestListener;
    private UpdateViewListener updateViewListener;

    /**
     * Default constructor that initialize port variable, create a new instance of {@link Driver}
     * and execute method initFolders
     *
     * @param port credential to running a server on a specific port
     * @see Server#initFolders()
     * @see Driver
     */
    Server(int port){
        this.port = port;
        try {
            new Driver();
        }catch (SQLException e){
            e.printStackTrace();
        }
        initFolders();
    }

    /**
     * This is linked to {@link WindowServer}, so when user click on START button
     * it will be execute
     * This method allow to receive request for a large number of clients
     * Than, when has been received a request, the instance will be pass to {@link StreamSocket}
     * @see ServerSocket
     */
    void initServer(){
        try {
            serverSocket = new ServerSocket(port);
            setLogView("SERVER IS RUNNING\n");
            writeToLogFile("SERVER STARTED");
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
                    }catch (Exception ex){
                        setLogView("SERVER STOPPED\n");
                        writeToLogFile("SERVER STOPPED");
                    }
                }
            }).start();
        }catch (Exception e){
            setLogView("PROBLEMS TO START SERVER\n");
            writeToLogFile("PROBLEMS TO START SERVER");
        }
    }

    /**
     * This method allow to attach this class to {@link StreamSocket}
     * @param streamSocket this is a {@link StreamSocket} object
     */
    private void addListenerToConnection(StreamSocket streamSocket){
        streamSocket.addSocketListener(this);
    }

    /**
     * This is a callback method, implemented by {@link StreamSocketListener}
     * This will be execute when an instance of {@link StreamSocket} will require to join into chat
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketListener
     * @see StreamSocket
     * @see StreamSocketEvent
     */
    @Override
    public void onJoinGroupRequest(StreamSocketEvent e) {
        serviceJoinGroupRequest(e);
    }

    /**
     * This method will be called after onJoinGroupRequest to consuming the specific request from {@link StreamSocket}
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketEvent
     */
    private synchronized void serviceJoinGroupRequest(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {
                arrayStreamSocket.add(e.getStreamSocket());
                arrayStreamSocket.lastElement().setIdConnection(arrayStreamSocket.size()-1);
                updateNumOfClients(arrayStreamSocket.size());

                setLogView("ACCEPTED REQUEST FROM "+arrayStreamSocket.lastElement().getSocket().getInetAddress().toString()+'\n');
                writeToLogFile("ACCEPTED REQUEST FROM "+arrayStreamSocket.lastElement().getSocket().getInetAddress().toString());

                notificationNewClient(arrayStreamSocket.lastElement().infoClient());
                MessageObject messageObject = new MessageObject(MessageObject.RequestType.ADD_ONLINE_USER);
                messageObject.setUserName(e.getNameClient());

                for(int i=0;i<arrayStreamSocket.size();i++){
                    try {
                        if(!arrayStreamSocket.elementAt(i).getUserClient().equals(e.getNameClient()))
                            arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }

                }
            }
        }).start();
    }

    /**
     * This is a callback method, implemented by {@link StreamSocketListener}
     * This will be execute when an instance of {@link StreamSocket} will require to disconnect from chat
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketListener
     * @see StreamSocket
     * @see StreamSocketEvent
     */
    @Override
    public void onRequestDeleteConnection(StreamSocketEvent e) {
        serviceCommunicationDeleting(e);
    }

    /**
     * This method will be called after onRequestDeleteConnection to consuming the specific request from {@link StreamSocket}
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketEvent
     */
    private synchronized void serviceCommunicationDeleting(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {

                setLogView("DISCONNECTION FROM "+e.getNameClient()+'\n');
                writeToLogFile("DISCONNECTION FROM "+e.getNameClient());

                notificationRemoveClient(e.getIdClient());
                arrayStreamSocket.removeElementAt(e.getIdClient());
                updateNumOfClients(arrayStreamSocket.size());

                MessageObject messageObject = new MessageObject(MessageObject.RequestType.REMOVE_ONLINE_USER);
                messageObject.setUserName(e.getNameClient());
                for(int i=0;i<arrayStreamSocket.size();i++){
                    arrayStreamSocket.elementAt(i).setIdConnection(i);
                    try {
                        arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                    }catch (IOException xe){
                        xe.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * This is a callback method, implemented by {@link StreamSocketListener}
     * This will be execute when an instance of {@link StreamSocket} will require to send a message to chat
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketListener
     * @see StreamSocket
     * @see StreamSocketEvent
     */
    @Override
    public void onReceiveMessage(StreamSocketEvent e) {
        serviceCommunicationMessage(e);
    }

    /**
     * This method will be called after onReceiveMessage to consuming the specific request from {@link StreamSocket}
     * @param e specific Event object that include information about sender;
     *
     * @see StreamSocketEvent
     */
    private synchronized void serviceCommunicationMessage(final StreamSocketEvent e){
        new Thread(new Runnable() {
            public void run() {
                String log = e.getMessage()+" SEND BY "+
                        arrayStreamSocket.elementAt(e.getIdClient()).getSocket().getInetAddress().toString()+" USERNAME: "+
                        arrayStreamSocket.elementAt(e.getIdClient()).getUserClient().toUpperCase();
                setLogView(log+'\n');
                writeToLogFile(log);
                for(int i=0;i<arrayStreamSocket.size();i++){
                    if(i != e.getIdClient()){
                        try {
                            MessageObject messageObject = new MessageObject(MessageObject.RequestType.SEND_MESSAGE);
                            messageObject.setUserName(e.getNameClient());
                            messageObject.setMessage(e.getMessage());
                            arrayStreamSocket.elementAt(i).getOutputStream().writeObject(messageObject);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * This is linked to {@link WindowServer}, so when user click on STOP button
     * it will be execute
     * This method allow to disconnect che server and to close all communications whit {@link StreamSocket}
     */
    void disconnect(){
        try {
            serverSocket.close();
            connected = false;
            for(int i=0;i<arrayStreamSocket.size();i++){
                arrayStreamSocket.elementAt(i).getOutputStream()
                        .writeObject(new MessageObject(MessageObject.RequestType.DISCONNECTION_FROM_SERVER));
                arrayStreamSocket.elementAt(i).disconnectionSocket();
            }
            arrayStreamSocket.removeAllElements();
            notificationRemoveAllClients();
            updateNumOfClients(arrayStreamSocket.size());

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    void addClientListener(RequestListener requestListener){
        this.requestListener = requestListener;
    }

    void addUpdateViewListener(UpdateViewListener updateViewListener){this.updateViewListener = updateViewListener;}

    private void notificationNewClient(String[] infoClient){
        RequestEvent requestEvent = new RequestEvent(this);
        requestEvent.setAddress(infoClient[0]);
        requestEvent.setName(infoClient[1]);
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
        requestEvent.setIndexOfClient(i);
        requestListener.onDisconnectionRequest(requestEvent);
    }

    private void notificationRemoveAllClients(){
        RequestEvent requestEvent = new RequestEvent(this);
        requestEvent.removeAllClients();
        requestListener.onDisconnectionRequest(requestEvent);
    }

    /**
     * This method allow to keep all logs into a specific system folder
     * This method will create a folder if don't exist
     */
    private void initFolders(){
        String OS = System.getProperty("os.name").toLowerCase();

        if (OS.contains("win")) {
            Path path = Paths.get("C:\\typing\\log");
            if(!Files.exists(path)){
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Path path = Paths.get("/etc/typing/log");
            if(!Files.exists(path)){
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This is the method that write in asynchronous way all logs
     * @param log specific string that will be write on system log
     */
    private void writeToLogFile(final String log){
        final Calendar calendar = Calendar.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileWriter fileWriter = null;
                try {
                    calendar.setTime(new Date(System.currentTimeMillis()));
                    if(!System.getProperty("os.name").toLowerCase().contains("win"))
                        fileWriter = new FileWriter("/etc/typing/log/typing_server_log.txt",true);
                    else
                        fileWriter = new FileWriter("C:\\typing\\log\\typing_server_log.txt",true);

                    fileWriter.write(
                            log.toUpperCase()
                            + "\n\t At " + calendar.get(Calendar.HOUR_OF_DAY) + "-" + calendar.get(Calendar.MINUTE)
                            + " In " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/"
                                    + calendar.get(Calendar.YEAR)
                            +"\n");

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(fileWriter != null)
                            fileWriter.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
