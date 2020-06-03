package server;

import common.Protocol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;
    private Server server;
    private DataOutputStream dos;
    private String userName;

    public Client(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(this.socket.getInputStream());
            dos = new DataOutputStream(this.socket.getOutputStream());
            connect(dis);
            while (true) {
                processMessage(dis);
            }
        } catch (IOException e) {

        } finally {
            if(userName!=null) {
                this.server.removeUser(userName);
            }
        }
    }

    public void connect(DataInputStream dis)  {
        try {
            byte type = dis.readByte();
            if (type == Protocol.HANDSHAKE) {
                userName = dis.readUTF();
                if (!this.server.addClient(userName, this)) {
                    userName = null;
                    this.dos.writeByte(Protocol.ERROR_INVALID_USER);
                    socket.close();
                    return;
                }
            }
        }
        catch (IOException e){
        }
        }

    public void processMessage(DataInputStream dis) throws IOException {
            byte type = dis.readByte();
            switch (type) {
                case (Protocol.GENERAL_MSG):
                    String text = dis.readUTF();
                    this.server.sendGeneralMsg(userName, text);
                    break;
                case (Protocol.PRIVATE_MSG):
                    String receiver = dis.readUTF();
                    text = dis.readUTF();
                    this.server.sendPrivateMsg(userName, receiver, text);
            }
        }


    public void removeUser(String userName) {
        try {
            this.dos.writeByte(Protocol.REMOVE_USER);
            this.dos.writeUTF(userName);
        } catch (IOException e) {

        }
    }

    public void addUser(String userName) {
        try {
            this.dos.writeByte(Protocol.ADD_USER);
            this.dos.writeUTF(userName);
        } catch (IOException e) {

        }
    }

    public void sendGeneralMsg(String userName, String text) {
        try {
            dos.writeByte(Protocol.GENERAL_MSG);
            dos.writeUTF(userName);
            dos.writeUTF(text);
        } catch (IOException e) {

        }
    }

    public void sendPrivateMsg(String sender, String text) {
        try {
            dos.writeByte(Protocol.PRIVATE_MSG);
            dos.writeUTF(sender);
            dos.writeUTF(text);
        } catch (IOException e){

        }
    }
}
