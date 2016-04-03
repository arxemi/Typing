package client;

import message.MessageObject;
import sun.awt.OSInfo;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.util.Vector;

/**
 * Progetto iniziato il 16/01/16
 * Sviluppato da acciaro emilio
 * Stato: work in progress
 * Versione: 4.2
 */

public class WindowClient extends JFrame implements ClientServiceListener{
    //java version supported
    private static final float JAVA_VERSION_SUPPORTED = 1.6F;
    //indirizzo e porta server
    private final String ADDRESS_NAME = "localhost";
    private final int ADDRESS_PORT = 6789;
    //style
    private Font font_temp_title = new Font("Verdana",Font.BOLD,22);
    private Font font_temp_btn = new Font("Verdana",Font.BOLD,20);
    private Font font_temp_label = new Font("Verdana",Font.BOLD,11);
    private Color color_foreground_title = Color.white;
    private Color color_background_panel = new Color(41, 38, 40);
    private Color color_foreground_lbl = Color.white;
    private Color color_background_btn = new Color(255, 102, 0);
    private Color color_background_btn_hover = new Color(166, 73, 0);
    private Color color_foreground_btn = Color.WHITE;
    //private Color color_foreground_hover_lbl = new Color(79, 102,255);
    private Color color_foreground_hover_lbl = new Color(0x196DC0);
    private Dimension btn_size = new Dimension(205,35);

    // definizione ed istanziamento dei 4 panelli principali più il cardlayout per switchare tra i panel
    private JPanel mainPanel = new JPanel();
    private JPanel loginPanel = new JPanel();
    private JPanel signupPanel = new JPanel();
    private JPanel chatPanel = new JPanel();
    private CardLayout cardLayout = new PageViewer(); //CardLayout modificato, con auto-resizing

    private JTextArea outputMessage;
    private DefaultListModel<String > defaultListModel = new DefaultListModel<String >();

    //private boolean connection_status = false;
    private Client client = null;

    public WindowClient(){
        super("");
        mainPanel.setLayout(cardLayout);
        mainPanel.add(loginPanel, "LOG_IN");
        mainPanel.add(signupPanel, "SIGN_UP");
        mainPanel.add(chatPanel, "CHAT");

        init_components_login_panel();
        init_components_signup_panel();
        init_components_chat_panel();

        //first panel to see
        add(mainPanel);
        cardLayout.show(mainPanel,"LOG_IN");

        setResizable(false);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  super.windowClosing(e);
                  if(client instanceof Client)
                      client.disconnect(MessageObject.requestType.DISCONNECTION_FROM_CLIENT);

              }
        });
    }

    private void initConnection(String welcome){
        outputMessage.append(welcome + "\n");
        outputMessage.setCaretPosition(outputMessage.getDocument().getLength());
        client.setClientServiceListener(this);
        client.startClientService();
    }

    @Override
    public void onReciveMessageFromServer(ClientServiceEvent e) {
        outputMessage.append(e.getMessage()+"\n");
        outputMessage.setCaretPosition(outputMessage.getDocument().getLength());
    }

    @Override
    public void addClientToList(ClientServiceEvent e) {
        if(e.getClientsOnline() instanceof Vector){
            for(int i=0;i<e.getClientsOnline().size();i++){
                defaultListModel.addElement(e.getClientsOnline().elementAt(i));
            }
        }else if(e.getNameClient() instanceof String){
            defaultListModel.addElement(e.getNameClient());
        }
    }

    @Override
    public void removeClientFromList(ClientServiceEvent e) {
        if(e.getNameClient() instanceof String){
            for(int i=0;i<defaultListModel.getSize();i++){
                if(defaultListModel.getElementAt(i).equals(e.getNameClient())){
                    defaultListModel.removeElementAt(i);
                    break;
                }
            }
        }else{
            defaultListModel.removeAllElements();
        }

    }

    private void initDisconnection(MessageObject.requestType type){
        client.disconnect(type);
        client = null;
    }

    private void init_components_login_panel(){
        //aggiunta componenti al pannello log in
        JLabel title = new JLabel("Login to chat Room");
        title.setFont(font_temp_title);
        title.setForeground(color_foreground_title);
        JPanel pnl_north = new JPanel(new FlowLayout(FlowLayout.CENTER,25,15));
        pnl_north.setBackground(color_background_panel);
        pnl_north.add(title);
        loginPanel.setLayout(new BorderLayout());
        loginPanel.add(pnl_north, BorderLayout.NORTH);

        JPanel temp_panel = new JPanel(new GridLayout(4,1,5,5));
        temp_panel.setBackground(color_background_panel);
        JLabel lbl_u = new JLabel("User");
        lbl_u.setFont(font_temp_label);
        lbl_u.setForeground(color_foreground_lbl);
        temp_panel.add(lbl_u);
        final JTextField txt_user = new JTextField(15);
        txt_user.setPreferredSize(new Dimension(150,25));
        temp_panel.add(txt_user);
        JLabel lbl_p = new JLabel("Password");
        lbl_p.setFont(font_temp_label);
        lbl_p.setForeground(color_foreground_lbl);
        temp_panel.add(lbl_p);
        final JPasswordField txt_psswd = new JPasswordField(15);
        temp_panel.add(txt_psswd);
        final JButton btn_login = new JButton("Login");
        btn_login.setBackground(color_background_btn);
        btn_login.setPreferredSize(btn_size);
        btn_login.setForeground(color_foreground_btn);
        btn_login.setFont(font_temp_btn);
        JPanel pnl_center = new JPanel(new BorderLayout());
        pnl_center.setBackground(color_background_panel);
        JPanel pnl_inner_one = new JPanel(new FlowLayout(FlowLayout.CENTER,5,10));
        JPanel pnl_inner_two = new JPanel(new FlowLayout(FlowLayout.CENTER,5,10));
        pnl_inner_one.setBackground(color_background_panel);
        pnl_inner_two.setBackground(color_background_panel);
        pnl_inner_one.add(temp_panel);
        pnl_inner_two.add(btn_login);
        pnl_center.add(pnl_inner_one,BorderLayout.NORTH);
        pnl_center.add(pnl_inner_two,BorderLayout.SOUTH);
        loginPanel.add(pnl_center,BorderLayout.CENTER);

        btn_login.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn_login.setBackground(color_background_btn_hover);
            }

            public void mouseExited(MouseEvent e) {
                btn_login.setBackground(color_background_btn);
            }
            public void mouseClicked(MouseEvent e) {
                try {
                    client = new Client(ADDRESS_NAME,ADDRESS_PORT,txt_user.getText(),new String(txt_psswd.getPassword()), MessageObject.requestType.LOG_IN);
                    MessageObject messageObject = client.getResponseFromServer();
                    switch (messageObject.getType()){
                        case REQUEST_VALIDATED:
                            initConnection(messageObject.getMessage());
                            client.updateNameOfClientsOnline(MessageObject.requestType.ADD_ONLINE_USER,messageObject.getListOnlineClients());
                            cardLayout.show(mainPanel,"CHAT");
                            pack();
                            setLocationRelativeTo(null);
                            break;
                        case REQUEST_NOT_VALIDATED:
                            JOptionPane.showMessageDialog(null,"Login non effettuato, controllare i paramentri inseriti!","Errore",JOptionPane.ERROR_MESSAGE);
                            break;
                        case USER_ALREADY_CONNECTED:
                            JOptionPane.showMessageDialog(null,"L'utente risulta già connesso nella chat!","Attenzione",JOptionPane.WARNING_MESSAGE);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null,"Attenzione, problemi con la connessione al server!","Connessione",JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                }catch (Exception e1){
                    JOptionPane.showMessageDialog(null,"Attenzione, problemi con la connessione al server!","Connessione",JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel pnl_lbl_bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,20));
        pnl_lbl_bottom.setBackground(color_background_panel);
        final JLabel lbl_to_signup = new JLabel("Non hai un account? Registrati");
        lbl_to_signup.setFont(font_temp_label);
        lbl_to_signup.setForeground(color_foreground_lbl);
        lbl_to_signup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lbl_to_signup.setForeground(color_foreground_hover_lbl);
            }
            public void mouseExited(MouseEvent e) {
                lbl_to_signup.setForeground(color_foreground_lbl);
            }
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel,"SIGN_UP");
                pack();
                setLocationRelativeTo(null);
            }
        });
        pnl_lbl_bottom.add(lbl_to_signup);
        loginPanel.add(pnl_lbl_bottom,BorderLayout.SOUTH);
        loginPanel.setBackground(color_background_panel);

    }

    private void init_components_signup_panel(){
        JLabel title = new JLabel("Sign up to chat Room");
        title.setFont(font_temp_title);
        title.setForeground(color_foreground_title);
        JPanel pnl_north = new JPanel(new FlowLayout(FlowLayout.CENTER,25,15));
        pnl_north.setBackground(color_background_panel);
        pnl_north.add(title);
        signupPanel.setLayout(new BorderLayout());
        signupPanel.add(pnl_north,BorderLayout.NORTH);

        JPanel temp_panel = new JPanel(new GridLayout(6,1,5,5));
        JLabel lbl_u = new JLabel("User");
        lbl_u.setFont(font_temp_label);
        lbl_u.setForeground(color_foreground_lbl);
        temp_panel.add(lbl_u);

        final JTextField txt_user = new JTextField(15);
        txt_user.setPreferredSize(new Dimension(150,20));
        temp_panel.add(txt_user);

        JLabel lbl_p = new JLabel("Password");
        lbl_p.setFont(font_temp_label);
        lbl_p.setForeground(color_foreground_lbl);
        temp_panel.add(lbl_p);

        final JPasswordField txt_psswd = new JPasswordField(15);
        temp_panel.add(txt_psswd);
        temp_panel.setBackground(color_background_panel);
        //
        JLabel lbl_p_c = new JLabel("Confirm password");
        lbl_p_c.setFont(font_temp_label);
        lbl_p_c.setForeground(color_foreground_lbl);
        temp_panel.add(lbl_p_c);

        final JPasswordField txt_psswd_c = new JPasswordField(15);
        temp_panel.add(txt_psswd_c);
        temp_panel.setBackground(color_background_panel);
        JPanel pnl_center= new JPanel(new BorderLayout());
        pnl_center.setBackground(color_background_panel);
        JPanel pnl_temp_inner0 = new JPanel(new FlowLayout(FlowLayout.CENTER,5,10));
        pnl_temp_inner0.setBackground(color_background_panel);
        pnl_temp_inner0.add(temp_panel);
        pnl_center.add(pnl_temp_inner0, BorderLayout.NORTH);

        final JButton btn_signup = new JButton("Sign up");
        btn_signup.setBackground(color_background_btn);
        btn_signup.setPreferredSize(btn_size);
        btn_signup.setForeground(color_foreground_btn);
        btn_signup.setFont(font_temp_btn);
        JPanel pnl_temp_inner = new JPanel(new FlowLayout(FlowLayout.CENTER,5,10));
        pnl_temp_inner.setBackground(color_background_panel);
        pnl_temp_inner.add(btn_signup);
        pnl_center.add(pnl_temp_inner,BorderLayout.SOUTH);

        signupPanel.add(pnl_center,BorderLayout.CENTER);

        btn_signup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn_signup.setBackground(color_background_btn_hover);
            }

            public void mouseExited(MouseEvent e) {
                btn_signup.setBackground(color_background_btn);
            }

            public void mouseClicked(MouseEvent e) {
                if(txt_user.getText().length()>1){
                    try {
                        client = new Client(ADDRESS_NAME,ADDRESS_PORT,txt_user.getText(),new String(txt_psswd.getPassword()), MessageObject.requestType.SIGN_UP);
                        MessageObject messageObject = client.getResponseFromServer();
                        switch (messageObject.getType()){
                            case REQUEST_VALIDATED:
                                initConnection(messageObject.getMessage());
                                client.updateNameOfClientsOnline(MessageObject.requestType.ADD_ONLINE_USER,messageObject.getListOnlineClients());
                                cardLayout.show(mainPanel,"CHAT");
                                pack();
                                setLocationRelativeTo(null);
                                break;
                            case REQUEST_NOT_VALIDATED:
                                JOptionPane.showMessageDialog(null,"Esiste già un utente con questo nome!","Attenzione",JOptionPane.WARNING_MESSAGE);
                                break;
                            default:
                                JOptionPane.showMessageDialog(null,"Attenzione, problemi con la connessione al server!","Connessione",JOptionPane.WARNING_MESSAGE);
                                break;
                        }
                    }catch (Exception e1){
                        JOptionPane.showMessageDialog(null,"Attenzione, problemi con la connessione al server!","Connessione",JOptionPane.WARNING_MESSAGE);
                    }
                }else {
                    JOptionPane.showMessageDialog(null,"Attenzione, sembra che alcuni campi non siano inseriti e/o le password non coincidano!","Parametri",JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        JPanel pnl_lbl_bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,20));
        pnl_lbl_bottom.setBackground(color_background_panel);

        final JLabel lbl_to_signup = new JLabel("Hai già un account? Accedi");
        lbl_to_signup.setFont(font_temp_label);
        lbl_to_signup.setForeground(color_foreground_lbl);
        lbl_to_signup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lbl_to_signup.setForeground(color_foreground_hover_lbl);
            }
            public void mouseExited(MouseEvent e) {
                lbl_to_signup.setForeground(color_foreground_lbl);
            }
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel,"LOG_IN");
                pack();
                setLocationRelativeTo(null);
            }
        });

        pnl_lbl_bottom.add(lbl_to_signup);
        signupPanel.add(pnl_lbl_bottom,BorderLayout.SOUTH);
        signupPanel.setBackground(color_background_panel);
    }

    private void init_components_chat_panel(){
        JPanel pnl_center = new JPanel();
        JPanel pnl_south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        outputMessage = new JTextArea(33,28);
        outputMessage.setEditable(false);

        JScrollPane jsp_outputMessage = new JScrollPane(outputMessage);
        jsp_outputMessage.setHorizontalScrollBar(null);

        final CustomTextField txt_input_message = new CustomTextField(21);
        txt_input_message.setPlaceholder(" Scrivi qualcosa");

        final JButton button_send = new JButton("Invia");
        button_send.setPreferredSize(new Dimension(75,30));
        button_send.setBackground(color_background_btn);
        button_send.setForeground(color_foreground_btn);
        button_send.setFont(new Font("Verdana",Font.BOLD,16));

        pnl_center.add(jsp_outputMessage);
        pnl_south.add(txt_input_message);
        pnl_south.add(button_send);
        pnl_center.setBackground(color_background_panel);
        pnl_south.setBackground(color_background_panel);

        JPanel pnl_left = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));//new FlowLayout(FlowLayout.LEFT,5,5));
        pnl_left.setBackground(color_background_panel);
        pnl_left.setPreferredSize(new Dimension(120,pnl_left.getHeight()));

        JLabel lbl_online_clients = new JLabel("Utenti online");
        lbl_online_clients.setForeground(color_foreground_lbl);
        lbl_online_clients.setFont(new Font("Verdana",Font.BOLD,14));
        pnl_left.add(lbl_online_clients);

        final JList<String> online_clients_list = new JList<String>(defaultListModel);
        online_clients_list.setFixedCellWidth(100);
        online_clients_list.setFixedCellHeight(30);

        online_clients_list.setBackground(color_background_panel);
        online_clients_list.setCellRenderer(new CustomListCell());

        button_send.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button_send.setBackground(color_background_btn_hover);
            }
            public void mouseExited(MouseEvent e) {
                button_send.setBackground(color_background_btn);
            }
            public void mouseClicked(MouseEvent e) {
                String stringa = txt_input_message.getText();
                if(stringa.length() > 0 && !stringa.isEmpty()){
                    try {
                        MessageObject messageObject = new MessageObject(MessageObject.requestType.SEND_MESSAGE);
                        messageObject.setMessage(stringa);
                        client.getOutputStream().writeObject(messageObject);
                        outputMessage.append("TU: "+stringa+"\n");
                        outputMessage.setCaretPosition(outputMessage.getDocument().getLength());
                        txt_input_message.customizeText(" Scrivi qualcosa");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

        JScrollPane scrollPane_online_clients = new JScrollPane();
        scrollPane_online_clients.getViewport().setView(online_clients_list);
        pnl_left.add(scrollPane_online_clients);

        final JButton button_logoff = new JButton("Logout");
        button_logoff.setBackground(color_background_btn);
        button_logoff.setFont(new Font("Verdana",Font.BOLD,16));
        button_logoff.setForeground(color_foreground_btn);
        button_logoff.setPreferredSize(new Dimension(100,30));
        button_logoff.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int replay = JOptionPane.showConfirmDialog(null,"Sicuro di voler chiudere la connessione?","Logout",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                if(replay==0){
                    initDisconnection(MessageObject.requestType.DISCONNECTION_FROM_CLIENT);
                    defaultListModel.removeAllElements();
                    outputMessage.setText("");
                    cardLayout.show(mainPanel,"LOG_IN");
                    pack();
                    setLocationRelativeTo(null);
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button_logoff.setBackground(color_background_btn_hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button_logoff.setBackground(color_background_btn);
            }
        });

        pnl_left.add(button_logoff);

        JLabel lbl_title_chat = new JLabel("Welcome to chat room");
        lbl_title_chat.setForeground(color_foreground_title);
        lbl_title_chat.setFont(font_temp_title);
        JPanel pnl_north  = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        pnl_north.setBackground(color_background_panel);
        pnl_north.add(lbl_title_chat);

        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(color_background_panel);
        chatPanel.add(pnl_north,BorderLayout.NORTH);
        chatPanel.add(pnl_left,BorderLayout.WEST);
        chatPanel.add(pnl_center,BorderLayout.EAST);
        chatPanel.add(pnl_south,BorderLayout.SOUTH);

    }

    public static void main(String[] arg){
        float version = Float.parseFloat(System.getProperty("java.version").substring(0,3));
        if(version>=JAVA_VERSION_SUPPORTED){
            try {
                if(OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)){
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                }else if(OSInfo.getOSType().equals(OSInfo.OSType.LINUX)){
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                }
            } catch (Exception e){}
            Runnable init = new Runnable() {
                public void run() {
                    new WindowClient();
                }
            };
            SwingUtilities.invokeLater(init);
        }else {
            JOptionPane.showMessageDialog(null,"Errore, versione di java non supportata!","Errore java version",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private class PageViewer extends CardLayout {

        public Dimension preferredLayoutSize(Container parent) {
            Component current = findCurrentComponent(parent);
            if (current != null) {
                Insets insets = parent.getInsets();
                Dimension pref = current.getPreferredSize();
                pref.width += insets.left + insets.right;
                pref.height += insets.top + insets.bottom;
                return pref;
            }
            return super.preferredLayoutSize(parent);
        }

        public Component findCurrentComponent(Container parent) {
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }
}