package server;

import server.listener.RequestListener;
import server.listener.UpdateViewListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author emilio acciaro on 20/01/16.
 */

public class WindowServer extends JFrame {

    //java minimum version supported
    private static final float MINIMUM_JAVA_VERSION_SUPPORTED = 1.7F;

    private JButton control_button = new JButton("Start server");
    private JLabel server_state = new JLabel("STATE: OFF");

    private DefaultTableModel tableModel = new DefaultTableModel(0, 0){
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTextArea txtArea = new JTextArea(5,40);
    private JLabel numOfClients = new JLabel("Number of clients: 0");

    private Server mainServer = new Server(6789);
    private Thread service;

    private boolean serverIsRunning = false;



    private WindowServer(){
        super("Server - Typing panel control");

        final Color color_background_panel = new Color(41, 38, 40);
        final Font font_temp_label = new Font("Verdana",Font.BOLD,12);

        JPanel global_panel = new JPanel(new BorderLayout());
        JPanel pnl_left = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        JPanel pnl_right = new JPanel(new FlowLayout(FlowLayout.LEADING,20,5));

        global_panel.setBackground(color_background_panel);
        pnl_left.setBackground(color_background_panel);
        pnl_right.setBackground(color_background_panel);
        control_button.setFont(font_temp_label);
        numOfClients.setFont(font_temp_label);
        server_state.setFont(font_temp_label);
        txtArea.setFont(font_temp_label);

        JTable table = new JTable();

        table.setFont(font_temp_label);

        JScrollPane spnTextArea = new JScrollPane(txtArea);
        JScrollPane spnTable = new JScrollPane();

        numOfClients.setForeground(Color.WHITE);

        tableModel.setColumnIdentifiers(new String[]{"Address","Username"});
        table.setModel(tableModel);
        spnTable.getViewport().add(table);
        add(BorderLayout.EAST,spnTable);
        add(BorderLayout.WEST,spnTextArea);
        pnl_left.add(numOfClients);
        pnl_right.add(server_state);
        pnl_right.add(control_button);
        global_panel.add(BorderLayout.WEST,pnl_left);
        global_panel.add(BorderLayout.EAST,pnl_right);
        add(BorderLayout.SOUTH,global_panel);
        txtArea.setEditable(false);
        server_state.setForeground(Color.RED);

        mainServer.addClientListener(new RequestListener() {
            public void onConnectionRequest(RequestEvent e) {
                tableModel.addRow(e.getClientValues());
            }
            public void onDisconnectionRequest(RequestEvent e) {
                if(!e.removeAllClients)
                    tableModel.removeRow(e.getIndexOfClient());
                else{
                    tableModel.setRowCount(0);
                }
            }
        });

        mainServer.addUpdateViewListener(new UpdateViewListener() {
            public void onRequestUpdateViewLog(UpdateViewEvent e) {
                txtArea.append(e.getLog());
                txtArea.setCaretPosition(txtArea.getDocument().getLength());
            }
            public void onRequestUpdateViewClients(UpdateViewEvent e) {
                numOfClients.setText("Number of clients: "+e.getNumOfClients());
            }
        });

        control_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(!serverIsRunning){
                    txtArea.setText("");
                    service = new Thread(new Runnable() {
                        public void run(){
                            mainServer.initServer();
                        }
                    });
                    service.start();
                    serverIsRunning = true;
                    server_state.setText("STATE: ON");
                    server_state.setForeground(new Color(0,160,0));
                    control_button.setText("Stop server");
                }
                else{
                    mainServer.disconnect();
                    numOfClients.setText("Number of clients: 0");
                    serverIsRunning = false;
                    server_state.setText("STATE: OFF");
                    server_state.setForeground(Color.RED);
                    control_button.setText("Start server");
                }
            }
        });
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] arg){

        float version = Float.parseFloat(System.getProperty("java.version").substring(0,3));
        if(version >= MINIMUM_JAVA_VERSION_SUPPORTED){
            try {
                javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e){
                e.printStackTrace();
            }
            Runnable init  = new Runnable() {
                public void run() {
                    new WindowServer();
                }
            };
            SwingUtilities.invokeLater(init);
        }else {
            JOptionPane.showMessageDialog(null,"Warning! Java version not supported!","Java version",
                    JOptionPane.WARNING_MESSAGE);
        }


    }
}