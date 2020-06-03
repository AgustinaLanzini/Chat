package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.common.Protocol;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class GUIUpdater implements Runnable {

    ChatGUI gui;
    Socket socket;

    public GUIUpdater(ChatGUI gui, Socket socket) {
        this.gui = gui;
        this.socket = socket;
    }

    @Override
    public void run() {
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
                        break;
                    case (Protocol.ERROR_INVALID_USER):
                        throw new IOException("Nombre de usuario repetido");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
