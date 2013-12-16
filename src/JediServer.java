import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * JediServer
 * 
 * @author Scott Christopher Stauffer
 */
public class JediServer extends javax.swing.JFrame {
    private ServerSocket server_sock;
    private ArrayList<JediServer.Connection> clients;
    private boolean stay_alive;
    private int port;
    public int client_count;
    
    
    public JediServer() {
        initComponents();
        initialize();
    }
    
    private void initialize() {
        clients = new ArrayList();
    }

    public void start_server() {
        stay_alive = true;

        try {
            server_sock = new ServerSocket(port);

            this.lblStatus.setText("Connected");
            this.lblStatus.setForeground(Color.green);
            
            while (stay_alive) {
                show_message("listening...");

                Socket sock = server_sock.accept();

                if (!stay_alive)
                    break;

                JediServer.Connection c = new JediServer.Connection(sock);
                
                update_users();
                
                clients.add(c);
                c.start();
            }

            disconnect();
            
            this.lblStatus.setText("Disconnected");
            this.lblStatus.setForeground(Color.red);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }
    
    protected void stop_server() {
        stay_alive = false;
        
        try {
            Socket s = new Socket("localhost", port);
        } catch (Exception e) {
            
        }
    }

    private void disconnect() {
        try {
            server_sock.close();		
            disconnect_all();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void disconnect_all() {
        for (int i = 0; i < clients.size(); ++i) {
            JediServer.Connection c = clients.get(i);

            try {
                if (c.input != null)
                    c.input.close();
                if (c.output != null)
                    c.output.close();
                if (c.sock != null)
                    c.sock.close();
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }
        
        clients.clear();
        clear_users();
    }

    private void show_message(String message) {
        this.txtChatView.append(message + "\r\n");
    }

    private synchronized void broadcast(String message) {
        show_message(message);

        for (int i = clients.size() - 1; i >= 0; i--) {
            JediServer.Connection c = clients.get(i);

            if (!c.send_message(message))
                this.remove_user(i);
        }
    }

    // TODO: Fix this
    private void clear_users() {
        /*DefaultListModel listModel = (DefaultListModel)lstConnections.getModel();
        listModel.removeAllElements();*/
    }
    
    // TODO: Fix this
    private void update_users() {
        /*DefaultListModel listModel = (DefaultListModel)lstConnections.getModel();
        
        for (JediServer.Connection c : clients)
            listModel.addElement(c.user);
        
        this.lblConnections.setText("Connections (" + clients.size() + ")");*/
    }
    
    public synchronized void remove_user(int id) {
        for (int i = 0; i < clients.size(); i++) {
            JediServer.Connection c = clients.get(i);

            if (c.id == id) {
                clients.remove(i);
                break;
            }
        }

        clear_users();
        update_users();
    }

    class Connection extends Thread {
        Socket sock;
        ObjectInputStream input;
        ObjectOutputStream output;
        String user;
        Message m;
        int id;

        Connection(Socket sock) {
            id = client_count++;
            this.sock = sock;

            try {
                output = new ObjectOutputStream(sock.getOutputStream());
                input  = new ObjectInputStream(sock.getInputStream());
                user = (String)input.readObject();
                show_message(user + " has connected");
                
                broadcast("[NEW_USER]:" + user);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            String message;
            boolean stay_alive = true;

            while (stay_alive) {
                try {
                    m = (Message)input.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println(e.getMessage());
                    break;
                }

                message = m.get_message();

                switch (m.get_type()) {
                    case Message.MESSAGE:
                        broadcast(user + ": " + message);
                    break;

                    case Message.DISCONNECT:
                        show_message(user + " has been disconnected");
                        broadcast("[USER_LEFT]:" + user);
                        stay_alive = false;
                    break;
                }
            }

            remove(id);
            close();
        }

        private void close() {
            try {
                if (output != null) 
                    output.close();
                if (input != null) 
                    input.close();
                if (sock != null)
                    sock.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        private boolean send_message(String message) {
            boolean pass = true;

            if (!sock.isConnected()) {
                close();
                pass = !pass;
            } else {				
                try {
                    output.writeObject(message);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }

            return pass;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstConnections = new javax.swing.JList();
        lblConnections = new javax.swing.JLabel();
        btnKick = new javax.swing.JButton();
        btnStartListening = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtChatView = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        btnDisconnect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jedIRC Server");
        setMaximumSize(new java.awt.Dimension(459, 322));
        setMinimumSize(new java.awt.Dimension(459, 322));
        setResizable(false);

        jScrollPane1.setViewportView(lstConnections);

        lblConnections.setText("Connections (0)");

        btnKick.setText("single user kick");
        btnKick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKickActionPerformed(evt);
            }
        });

        btnStartListening.setText("start listening");
        btnStartListening.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartListeningActionPerformed(evt);
            }
        });

        txtChatView.setColumns(20);
        txtChatView.setRows(5);
        jScrollPane3.setViewportView(txtChatView);

        jLabel3.setText("Chat View @ (irc)");

        jLabel1.setText("Port:");

        txtPort.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPort.setText("31337");

        btnDisconnect.setText("disconnect all");
        btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDisconnectActionPerformed(evt);
            }
        });

        jLabel2.setText("Status:");

        lblStatus.setForeground(new java.awt.Color(204, 0, 0));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatus.setText("Disconnected");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblConnections))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnStartListening, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtPort)))
                                    .addComponent(btnKick, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnDisconnect, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConnections)
                    .addComponent(jLabel2)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStartListening)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKick)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDisconnect))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnStartListeningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartListeningActionPerformed
        try {
            this.port = Integer.parseInt(this.txtPort.getText());
            
            if (this.port < 1 || this.port > 65535)
                throw new NumberFormatException();
            
            new Server().start();
        } catch (NumberFormatException nfe) {
            System.err.println(nfe.getMessage());
            
            JOptionPane.showMessageDialog(
                null, 
                "Server port must be within this range: 1-65535", 
                "jedIRC Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnStartListeningActionPerformed

    private void btnKickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKickActionPerformed
        
    }//GEN-LAST:event_btnKickActionPerformed

    private void btnDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDisconnectActionPerformed
        
    }//GEN-LAST:event_btnDisconnectActionPerformed

    class Server extends Thread {
        public void run() {
            start_server();
        }
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnKick;
    private javax.swing.JButton btnStartListening;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblConnections;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JList lstConnections;
    private javax.swing.JTextArea txtChatView;
    private javax.swing.JTextField txtPort;
    // End of variables declaration//GEN-END:variables
}
