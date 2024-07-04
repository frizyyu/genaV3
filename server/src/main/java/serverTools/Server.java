package serverTools;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {
    private final Integer port;
    private final ServerSocketChannel serverChannel;

    public Server(Integer port) throws IOException {
        this.port = port;

        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(port));
        this.serverChannel.configureBlocking(false);
    }

    public Integer getPort() {
        return this.port;
    }

    public ServerSocketChannel getServerChannel() {
        return this.serverChannel;
    }

    public void register(Selector selector, Integer key) throws ClosedChannelException {
        this.serverChannel.register(selector, key);
    }

    public void register(Selector selector, Integer key, Object attachment) throws ClosedChannelException {
        this.serverChannel.register(selector, key, attachment);
    }
}