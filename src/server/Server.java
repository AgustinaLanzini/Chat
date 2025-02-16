package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    private Map<String, Client> clients;
    private int port;

    public Server(int port) {
        this.port = port;
        this.clients = new HashMap<>();
    }

    public static void main(String[] args) {
        int port =  Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.listen();
    }

    public void listen() {
        Executor executor = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(this.port)){
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new Client(socket, this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean addClient(String userName, Client client) {
        if (this.clients.containsKey(userName)) {
            return false;
        }
        this.clients.put(userName, client);
        this.clients.entrySet()
                .parallelStream()
                .filter(entry -> !entry.getKey().equals(userName))
                .forEach(entry -> {entry.getValue().addUser(userName);
                                    client.addUser(entry.getKey());
                });

        return true;
    }

    public synchronized void removeUser(String userName) {
        this.clients.remove(userName);
        this.clients.values().forEach(c -> c.removeUser(userName));
    }

    public synchronized void sendGeneralMsg(String userName, String text) {
        this.clients.entrySet().parallelStream().
                filter( e -> !e.getKey().equals(userName)).
                forEach( e -> e.getValue().sendGeneralMsg(userName, text));
    }

    public synchronized void sendPrivateMsg(String sender, String receiver, String text){
        this.clients.get(receiver).sendPrivateMsg(sender, text);
    }


}
