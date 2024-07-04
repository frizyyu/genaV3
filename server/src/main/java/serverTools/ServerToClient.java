package serverTools;
import java.net.*;
import java.io.*;
import helpers.ConfigReader;
public class ServerToClient extends Thread{
    private ServerSocket serverSocket;

    public ServerToClient(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Ожидание клиента на порт " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();

                System.out.println("Просто подключается к " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                System.out.println(in.readUTF());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Спасибо за подключение к " + server.getLocalSocketAddress()
                        + "\nПока!");
                server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Время сокета истекло!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void startServer() {
        int port = Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("port"));
        try {
            Thread t = new ServerToClient(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
