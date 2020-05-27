package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.Protocol;

import java.io.DataInputStream;
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

    public void run() throws IOException{
        Socket socket = new Socket(host, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        gui = MainWindows.launchOrGet(new Callback(dos));
        dos.writeByte(Protocol.HANDSHAKE);
        dos.writeUTF(userName);
        updateGui(socket);
    }

    private void updateGui(Socket socket) {
        new Thread(()-> {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (true) {
                    byte type = dis.readByte();
                    switch (type) {
                        case (Protocol.ADD_USER):
                            String user = dis.readUTF();
                            gui.addUser(user);
                            break;
                        case (Protocol.REMOVE_USER):
                            user = dis.readUTF();
                            gui.removeUser(user);
                            break;
                        case (Protocol.GENERAL_MSG):
                            user = dis.readUTF();
                            String text = dis.readUTF();
                            gui.addNewGeneralMsg(user, text);
                            break;
                        case (Protocol.PRIVATE_MSG):
                            user = dis.readUTF();
                            text = dis.readUTF();
                            gui.addNewMsg(user, text);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(args[0], 6663, args[1]);
            c.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
