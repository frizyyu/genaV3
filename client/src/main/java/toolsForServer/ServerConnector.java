package toolsForServer;

import helpers.ConfigReader;
import java.io.IOException;

public class ServerConnector {
    public static ClientToServer clientToServer;

    public static void createConnection() throws IOException {
        clientToServer = new ClientToServer(ConfigReader.getInstance().getInfoFromConfig("serverAddress"), Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("serverPort")));
        clientToServer.connect();
    }
    public void setConnection(ClientToServer clientToServer){
        ServerConnector.clientToServer = clientToServer;
    }

    public static ClientToServer getConnection(){
        return clientToServer;
    }
}