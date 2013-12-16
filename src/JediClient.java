import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * JediClient
 * 
 * @author Scott Christopher Stauffer
 */
public class JediClient extends javax.swing.JFrame {
    private Socket sock;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String server, user;
    private int port;
    private ArrayList<String> users;
    
    public JediClient() {
        use_default_config();
        initialize();
        initComponents();
    }
    
    public JediClient(String server, int port, String user) {
        setup_client(server, user, port);
        initialize();
        initComponents();
    }
    
    private void initialize() {
        users = new ArrayList();
    }
    
    private void use_default_config() {
        setup_client("localhost", "anon", 31337);
    }
    
    private void setup_client(String server, String user, int port) {
        this.server = server;
        this.user = user;
        this.port = port;
    }

    private boolean create_socket() {
        boolean pass = true;

        try {
            sock = new Socket(server, port);
        } catch(Exception e) {
            System.err.println("could not create socket...");
            pass = !pass;
        }

        return pass;
    }

    private boolean create_streams() {
        boolean pass = true;

        try {
            input = new ObjectInputStream(sock.getInputStream());
            output = new ObjectOutputStream(sock.getOutputStream());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            pass = !pass;
        }

        return pass;
    }

    private boolean send_user() {
        boolean pass = true;

        try {
            output.writeObject(user);
        } catch (IOException ioe) {
            show_message(ioe.getMessage());
            disconnect();
            pass = !pass;
        }

        return pass;
    }

    private boolean start() {
        boolean pass = true;

        if (create_socket()) {
            show_message("connection established @ " + sock.getInetAddress() + ":" + sock.getPort());

            if (create_streams()) {
                new JediClient.Listener().start();

                if (send_user())
                    return pass;
                else
                    pass = !pass;
            } else
                pass = !pass;
        } else
            pass = !pass;

        return pass;
    }

    private void show_message(String message) {
        txtChatLog.append(message + "\r\n");
    }

    private void send_message(Message message) {
        try {
            output.writeObject(message);
        } catch(IOException ioe) {
            show_message(ioe.getMessage());
        }
    }
    
    private void connect(String arg) {
        String _user, _server;
        int _port;
        
        _user = arg.substring(0, arg.indexOf("@"));
        _server = arg.substring(arg.indexOf("@") + 1, arg.indexOf(":"));

        try {
            _port = Integer.parseInt(
                arg.substring(
                    arg.indexOf(":") + 1, 
                    arg.length() - 1
                )
            );
        } catch (Exception e) {
            System.err.println("invalid port number! using default...");
            _port = port;
        }
        
        setup_client(_server, _user, _port);

        if (!start())
            this.txtChatLog.append("[failure to connect]\r\n");
    }

    private void disconnect() {
        try { 
            if (input != null) 
                input.close();
            if (output != null) 
                output.close();
            if (sock != null) 
                sock.close();
            
            send_message(new Message(Message.DISCONNECT, ""));
            
            use_default_config();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    // TODO: Fix this
    private void update_user(String username) {
        users.add(username);

        JOptionPane.showMessageDialog(null, "getting list model", "update_user", 0);
        DefaultListModel listModel = (DefaultListModel)lstUsers.getModel();
        
        JOptionPane.showMessageDialog(null, "iterating users arraylist", "update_user", 0);
        for (Iterator<String> it = users.iterator(); it.hasNext();)
            listModel.addElement(it.next());
        
        txtChatLog.append(username + " has joined.\r\n");
    }
    
    class Listener extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String in = (String)input.readObject();
                    
                    if (in.startsWith("[NEW_USER]:")) { /* TODO: Handle this */}
                        //update_user(in.substring(in.indexOf(":")));
                    else
                       txtChatLog.append(in);
                } catch(IOException ioe) {
                    System.err.println(ioe.getMessage());
                    break;
                } catch(ClassNotFoundException cnfe) {
                    System.err.println(cnfe.getMessage());
                }
            }
        }
    }

    private void handle_text()
    {
        String message = txtMessage.getText();

        if (message.length() == 0)
            return;
        
        if (message.equals("/disconnect")) {
            disconnect();
            use_default_config();
        } else if (message.startsWith("/connect") && message.contains(":") && message.contains("@"))
            connect(message.split(" ", 2)[1]);
        else if (message.startsWith("/nick")) {
            // TODO: change nickname
        }
        else
            send_message(new Message(Message.MESSAGE, txtMessage.getText()));

        txtMessage.setText("");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tbpTabs = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtChatLog = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstUsers = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMessage = new javax.swing.JTextArea();
        btnSendMessage = new javax.swing.JButton();
        mnuJediClient = new javax.swing.JMenuBar();
        mnuMain = new javax.swing.JMenu();
        mnuDisconnect = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jedIRC");
        setMaximumSize(new java.awt.Dimension(617, 326));
        setMinimumSize(new java.awt.Dimension(617, 326));
        setResizable(false);

        txtChatLog.setColumns(20);
        txtChatLog.setRows(5);
        jScrollPane3.setViewportView(txtChatLog);

        tbpTabs.addTab("jedIRC", jScrollPane3);

        jScrollPane1.setViewportView(lstUsers);

        txtMessage.setColumns(20);
        txtMessage.setRows(5);
        txtMessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMessageKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(txtMessage);

        btnSendMessage.setText("Send");
        btnSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendMessageActionPerformed(evt);
            }
        });

        mnuMain.setText("JediClient");

        mnuDisconnect.setText("Disconnect");
        mnuDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDisconnectActionPerformed(evt);
            }
        });
        mnuMain.add(mnuDisconnect);

        mnuJediClient.add(mnuMain);

        mnuHelp.setText("Help");
        mnuHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuHelpMouseClicked(evt);
            }
        });
        mnuJediClient.add(mnuHelp);

        setJMenuBar(mnuJediClient);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(tbpTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbpTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)))
        );

        tbpTabs.getAccessibleContext().setAccessibleName("jedIRC");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuHelpMouseClicked
        JOptionPane.showMessageDialog(null, "Written by scstauf", "Java Jedi", 0);
    }//GEN-LAST:event_mnuHelpMouseClicked
    
    private void txtMessageKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMessageKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            handle_text();
    }//GEN-LAST:event_txtMessageKeyReleased

    private void mnuDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDisconnectActionPerformed
        disconnect();
        use_default_config();
    }//GEN-LAST:event_mnuDisconnectActionPerformed

    private void btnSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendMessageActionPerformed
        handle_text();
    }//GEN-LAST:event_btnSendMessageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSendMessage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstUsers;
    private javax.swing.JMenuItem mnuDisconnect;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuBar mnuJediClient;
    private javax.swing.JMenu mnuMain;
    private javax.swing.JTabbedPane tbpTabs;
    private javax.swing.JTextArea txtChatLog;
    private javax.swing.JTextArea txtMessage;
    // End of variables declaration//GEN-END:variables
}
