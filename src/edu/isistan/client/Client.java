package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.Protocol;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    ChatGUI gui;
    String host;
    int port;
    String userName;

    public Client(String host, int port, String username) {
        this.host = host;
        this.userName = username;
        this.port = port;
    }

    public void connect() throws IOException{
        Socket socket = new Socket(host, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeByte(Protocol.HANDSHAKE);
        dos.writeUTF(userName);
        this.gui = MainWindows.launchOrGet(new Callback(dos));
        Thread thread = new Thread(new GUIUpdater(gui, socket));
        thread.start();
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(args[0], 6663, args[1]);
            c.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
