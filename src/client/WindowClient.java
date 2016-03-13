package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by emilio on 16/01/16.
 */
public class WindowClient extends JFrame{
    JPanel pnlSouth = new JPanel();
    JPanel pnl_log_in = new JPanel(new GridLayout(2,2));
    JPanel pnl_sign_up = new JPanel(new GridLayout(3,2));

    JButton btn_connection = new JButton("Log-off");
    JTextArea txtArea = new JTextArea(20,10);
    JScrollPane spn = new JScrollPane(txtArea);
    JTextField txtName = new JTextField(10);
    JLabel lblMex = new JLabel("Scrivi un mex");
    JTextField txtMex = new JTextField(10);
    JButton btnSend = new JButton("Invia mex");

    private boolean connection_status = false;
    private Client client = null;

    public WindowClient(){
        super("Chat");
        txtArea.setEditable(false);
        add(BorderLayout.CENTER, spn);

        pnlSouth.setBackground(new Color(30,144,255));

        txtMex.setEnabled(false);
        btnSend.setEnabled(false);
        btn_connection.setEnabled(false);
        pnlSouth.add(lblMex);
        pnlSouth.add(txtMex);
        pnlSouth.add(btnSend);
        pnlSouth.add(btn_connection);
        add(BorderLayout.SOUTH,pnlSouth);

        btn_connection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(connection_status){
                    initDisconnection(1);
                    txtArea.setText("");
                    txtName.setText("");
                    pnl_sign_up.removeAll();
                    pnl_log_in.removeAll();
                    initPanelRequest();
                }
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String s = txtMex.getText();
                try {
                    client.outputStream.writeBytes(s+"\n");
                    txtArea.append("TU: "+s+"\n");
                    txtArea.setCaretPosition(txtArea.getDocument().getLength());
                    txtMex.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //setResizable(false);
        setVisible(true);
        setSize(480,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  super.windowClosing(e);
                  if(client instanceof Client)
                      client.disconnect(1);

              }
        });
    }

    public void initConnection(){
        connection_status = true;
        txtMex.setEnabled(true);
        btnSend.setEnabled(true);
        btn_connection.setEnabled(true);
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try{
                        String s = client.bufferedReader.readLine();
                        if(s.equals("hsh53wh2k2b38dnwy3nu3tdb38bd7eyf2debidg2")){
                            initDisconnection(2);
                            txtArea.setText("COMUNICAZIONE DI SERVIZIO: Server unreachable!");
                            break;
                        }
                        txtArea.append(s+"\n");
                        txtArea.setCaretPosition(txtArea.getDocument().getLength());
                    }catch (Exception e){}
                }
            }
        }).start();
    }

    public void initDisconnection(int code){
        client.disconnect(code);
        client = null;
        connection_status = false;
        txtMex.setEnabled(false);
        btnSend.setEnabled(false);
        btn_connection.setEnabled(false);
    }

    public void initPanelRequest(){
        String[] buttons = { "Log-in", "Sign-up", "Exit"};
        int r = JOptionPane.showOptionDialog(null, "Clicca su Log-in per effettuare l'accesso! Non sei registrato?" +
                " Clicca su sign-up!", "Chat", JOptionPane.INFORMATION_MESSAGE,JOptionPane.QUESTION_MESSAGE,
                null,buttons,buttons[0]);
        if(r == 0){
            String[] buttons_log_in = { "Log-in", "Cancel"};
            JTextField txt_user = new JTextField(15);
            JPasswordField txt_psswd = new JPasswordField(15);
            pnl_log_in.add(new JLabel("User"));
            pnl_log_in.add(txt_user);
            pnl_log_in.add(new JLabel("Password"));
            pnl_log_in.add(txt_psswd);

            int re = JOptionPane.showOptionDialog(null,pnl_log_in,"Login - form",JOptionPane.NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,null,buttons_log_in,buttons_log_in[0]);
            if(re == 0){
                String username=null,password=null;
                try{
                    username = txt_user.getText();
                    password = new String(txt_psswd.getPassword());
                }catch (Exception e){}
                client = new Client("localhost",6789,username,password,Client.LOG_IN_REQUEST);
                int answare_from_server = client.validateRequest();
                if(answare_from_server == 1){
                    initConnection();
                }else if(answare_from_server == 0) {
                    JOptionPane.showMessageDialog(null,"Errore, controllare i paramentri inseriti!",
                            "Errore",JOptionPane.ERROR_MESSAGE);
                    pnl_log_in.removeAll();
                    initPanelRequest();
                }else if(answare_from_server == 2){
                    JOptionPane.showMessageDialog(null,"Errore, l'utente con cui si tenta di accedere è già connesso!",
                            "Errore",JOptionPane.ERROR_MESSAGE);
                    pnl_log_in.removeAll();
                    initPanelRequest();
                }

            }else if(re == 1 || re == -1){
                pnl_log_in.removeAll();
                initPanelRequest();
            }
        }else if(r == 1){
            String[] buttons_log_in = { "Sign-up", "Cancel"};
            JTextField txt_user = new JTextField("",15);
            JPasswordField txt_psswd = new JPasswordField("",15);
            JPasswordField txt_c_psswd = new JPasswordField("",15);
            pnl_sign_up.add(new JLabel("User"));
            pnl_sign_up.add(txt_user);
            pnl_sign_up.add(new JLabel("Password"));
            pnl_sign_up.add(txt_psswd);
            pnl_sign_up.add(new JLabel("Conferma password"));
            pnl_sign_up.add(txt_c_psswd);

            int re = JOptionPane.showOptionDialog(null,pnl_sign_up,"Sign-up form",JOptionPane.NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,null,buttons_log_in,buttons_log_in[0]);
            if(re == 0){
                String username=null,password=null,confirmedPassword=null;
                try{
                    username = txt_user.getText();
                    password = new String(txt_psswd.getPassword());
                    confirmedPassword = new String(txt_c_psswd.getPassword());
                }catch (Exception e){}
                if(password.equals(confirmedPassword) && !username.isEmpty() && !password.isEmpty()) {
                    client = new Client("localhost", 6789, username,password, Client.SIGN_UP_REQUEST);
                    int answare_from_server = client.validateRequest();
                    if(answare_from_server == 1){
                        initConnection();
                    }else if(answare_from_server == 0){
                        JOptionPane.showMessageDialog(null,"Errore, username già utilizzato!",
                                "Errore",JOptionPane.ERROR_MESSAGE);
                        pnl_sign_up.removeAll();
                        initPanelRequest();
                    }
                }else {
                    JOptionPane.showMessageDialog(null,"Errore, verificare i parametri inseriti!",
                            "Errore",JOptionPane.ERROR_MESSAGE);
                    pnl_sign_up.removeAll();
                    initPanelRequest();
                }

            }else if(re == 1 || re == -1){
                pnl_sign_up.removeAll();
                initPanelRequest();
            }
        }else if(r == 2){
            System.exit(1);
        }
        else{
            initPanelRequest();
        }
    }

    public static void main(String[] arg){
        Runnable init = new Runnable() {
            public void run() {
                new WindowClient().initPanelRequest();
            }
        };
        SwingUtilities.invokeLater(init);

    }
}