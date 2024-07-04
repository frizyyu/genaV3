package exeption;
import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

public class ClientInterrupt extends SocketException {
    private final SocketChannel client;

    public ClientInterrupt(SocketChannel client) throws IOException {
        super(client.getRemoteAddress().toString());
        this.client = client;
    }

    public SocketChannel getClient() {
        return this.client;
    }
}