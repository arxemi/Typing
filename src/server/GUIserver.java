package server;

import server.listener_interface.RequestListener;
import server.listener_interface.UpdateViewListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by emilio on 20/01/16.
 */
public class GUIserver extends JFrame {
    private JPanel globalpanel = new JPanel(new BorderLayout());
    private JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT,30,10));
    private JPanel pnlNumberClients = new JPanel(new FlowLayout(FlowLayout.LEADING,5,5));
    private JButton btnControl = new JButton("Avvia server");
    private JLabel lblState = new JLabel("Stato: OFF");
    private DefaultTableModel dtm = new DefaultTableModel(0, 0){
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTable tbl = new JTable();

    private JTextArea txtArea = new JTextArea(5,40);
    private JLabel numOfClients = new JLabel("Numero di clients: 0");
    private JScrollPane spnTextArea = new JScrollPane(txtArea);
    private JScrollPane spnTable = new JScrollPane();
    private Server mainServer = new Server(6789);
    private Thread serivce;
    private boolean serverIsRunning = false;

    public GUIserver(){
        super("Server - Sistema di controllo");

        dtm.setColumnIdentifiers(new String[]{"Address","Name"});
        tbl.setModel(dtm);
        spnTable.getViewport().add(tbl);
        add(BorderLayout.EAST,spnTable);
        add(BorderLayout.WEST,spnTextArea);
        pnlNumberClients.add(numOfClients);
        pnl.add(lblState);
        pnl.add(btnControl);
        globalpanel.add(BorderLayout.WEST,pnlNumberClients);
        globalpanel.add(BorderLayout.EAST,pnl);
        add(BorderLayout.SOUTH,globalpanel);
        txtArea.setEditable(false);
        lblState.setForeground(Color.RED);

        mainServer.addClientListener(new RequestListener() {
            public void onConnectionRequest(ClientEvent e) {
                dtm.addRow(e.getClientValues());
            }
            public void onDisconnectionRequest(ClientEvent e) {
                if(!e.removeAllClients_)
                    dtm.removeRow(e.getIndexOfclient());
                else{
                    dtm.setRowCount(0);
                }
            }
        });

        mainServer.addUpdateViewListener(new UpdateViewListener() {
            public void onRequestUpdateViewLog(UpdateViewEvent e) {
                txtArea.append(e.getLog());
                txtArea.setCaretPosition(txtArea.getDocument().getLength());
            }
            public void onRequestUpdateViewClients(UpdateViewEvent e) {
                numOfClients.setText("Numero di clients: "+e.getNumOfClients());
            }
        });

        btnControl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(!serverIsRunning){
                    txtArea.setText("");
                    serivce = new Thread(new Runnable() {
                        public void run(){
                            mainServer.initServer();
                        }
                    });
                    serivce.start();
                    serverIsRunning = true;
                    lblState.setText("Stato: ON");
                    lblState.setForeground(new Color(0,160,0));
                    btnControl.setText("Arresta Server");
                }
                else if(serverIsRunning){
                    mainServer.disconnect();
                    numOfClients.setText("Numero di clients: 0");
                    serverIsRunning = false;
                    lblState.setText("Stato: OFF");
                    lblState.setForeground(Color.RED);
                    btnControl.setText("Avvia Server");
                }
            }
        });
        setVisible(true);
        setSize(920,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] arg){
        Runnable init  = new Runnable() {
            public void run() {
                new GUIserver();
            }
        };
        SwingUtilities.invokeLater(init);
    }
}