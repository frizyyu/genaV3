package toolsForServer;
import java.net.*;
import java.io.*;
public class ClientToServer {
    private final String serverAddress;
    private final int serverPort;
    public ClientToServer(String address, int port){
        serverAddress = address;
        serverPort = port;
    }
    public void connect() {
        try {
            System.out.println("Подключение к " + serverAddress + " на порт " +serverPort);
            Socket client = new Socket(serverAddress,serverPort);

            System.out.println("Просто подключается к " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Привет из " + client.getLocalSocketAddress());
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("Сервер ответил " + in.readUTF());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}