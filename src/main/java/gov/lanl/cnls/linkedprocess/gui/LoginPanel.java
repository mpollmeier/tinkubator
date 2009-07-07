package gov.lanl.cnls.linkedprocess.gui;

import gov.lanl.cnls.linkedprocess.xmpp.lopfarm.XmppFarm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import org.jivesoftware.smack.XMPPException;

/**
 * User: marko
 * Date: Jul 6, 2009
 * Time: 10:32:32 AM
 */
public class LoginPanel extends JPanel implements ActionListener {

    protected FarmGui farmGui;
    protected JTextField usernameField;
    protected JTextField passwordField;
    protected JTextField serverField;
    protected JTextField portField;
    protected JLabel statusLabel;
    protected JCheckBox rememberBox;
    protected Image backgroundImage;
    protected final static String PROPERTIES_FILE = "farm_manager.properties";

    public LoginPanel(FarmGui farmGui) {
        super(new BorderLayout());
        this.farmGui = farmGui;
        this.backgroundImage = FarmGui.farmBackground.getImage();
        this.setOpaque(false);

        this.usernameField = new JTextField("", 15);
        this.passwordField = new JPasswordField("", 15);
        this.serverField = new JTextField("", 15);
        this.portField = new JTextField("5222", 15);
        this.rememberBox = new JCheckBox("remember");
        this.rememberBox.setSelected(true);

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(PROPERTIES_FILE));
            this.usernameField.setText(props.getProperty("username"));
            this.passwordField.setText(props.getProperty("password"));
            this.serverField.setText(props.getProperty("server"));
            this.portField.setText(props.getProperty("port"));
        } catch(Exception e) {
            System.out.println("Could not load " + PROPERTIES_FILE + " file.");

        }

        JPanel mainPanel = new JPanel(new GridLayout(5,2,0,0));

        mainPanel.add(new JLabel("username:"));
        mainPanel.add(usernameField);

        mainPanel.add(new JLabel("password:"));
        mainPanel.add(passwordField);

        mainPanel.add(new JLabel("server:"));
        mainPanel.add(serverField);

        mainPanel.add(new JLabel("port:"));
        mainPanel.add(portField);

        mainPanel.add(new JLabel());
        mainPanel.add(this.rememberBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loginButton = new JButton("login");
        JButton quitButton = new JButton ("quit");
        this.statusLabel = new JLabel();
        buttonPanel.add(loginButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(statusLabel);

        mainPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        //this.add(new JLabel("http://linkedprocess.org (Los Alamos National Laboratory)"), BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(this);
        quitButton.addActionListener(this);

        this.setBorder(BorderFactory.createLineBorder(FarmGui.GRAY_COLOR, 2));


    }

    public void actionPerformed(ActionEvent event) {

        if(this.rememberBox.isSelected()) {
            Properties props = new Properties();
            props.put("username", this.usernameField.getText());
            props.put("password", this.passwordField.getText());
            props.put("server", this.serverField.getText());
            props.put("port", this.portField.getText());

            try {
                props.store(new FileOutputStream(PROPERTIES_FILE), "saved properties for simple linked process farm manager");
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            new File(PROPERTIES_FILE).delete();
        }
        
        try {
            if(event.getActionCommand().equals("login")) {
                this.statusLabel.setText("");
                XmppFarm farm = new XmppFarm(serverField.getText(), new Integer(this.portField.getText()), this.usernameField.getText(), this.passwordField.getText());
                this.farmGui.loadMainFrame(farm);
            } else {
                System.exit(0);
            }
        } catch(XMPPException e) {
            this.statusLabel.setText("Could not login.");
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, null);
        super.paintComponent(g);
    }
}
