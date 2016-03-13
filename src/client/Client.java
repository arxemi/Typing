package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by emilio on 16/01/16.
 */
public class Client {
    Socket socket;
    DataOutputStream outputStream;
    BufferedReader bufferedReader;
    private String nomeClient;
    private String indirizzo;
    private int port;
    private String psswd;
    static final int LOG_IN_REQUEST = 0;
    static final int SIGN_UP_REQUEST = 1;
    private final String REQUEST_VALIDATED = "501-fh4hjh45h4";
    private final String REQUEST_NOT_VALIDATED = "500-fh4hjh45h4";
    private final String USER_ALREADY_CONNECTED = "502-fh4hjh45h4";

    public Client(String indirizzo, int port, String nomeClient, String psswd, int request_type){
        this.nomeClient = nomeClient;
        this.psswd = psswd;
        this.indirizzo = indirizzo;
        this.port = port;
        try {
            socket = new Socket(indirizzo,port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream.writeBytes(request_type+"\n");
            outputStream.writeBytes(nomeClient+"\n");
            outputStream.writeBytes(psswd+"\n");
        }catch (Exception e){}
    }

    public int validateRequest(){
        int res = 0;
        try {
            String r = bufferedReader.readLine();
            if(r.equals(REQUEST_NOT_VALIDATED)){
                res = 0;
            }else if(r.equals(REQUEST_VALIDATED)){
                res = 1;
            }else if(r.equals(USER_ALREADY_CONNECTED)){
                res = 2;
            }
        }catch (Exception e){}
        return  res;
    }

    public void disconnect(int code){
        if(socket instanceof Socket){

            try {
                if(code == 1){
                    outputStream.writeBytes("b5ih45i54g5hk443hbk43b\n");
                }
                else if(code == 2){
                    outputStream.writeBytes("hsh53wh2k2b38dnwy3nu3tdb38bd7eyf2debidg2\n");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}