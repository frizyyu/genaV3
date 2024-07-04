package toolsForServer;

import helpers.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Client {
    private final String address;
    private final Integer port;
    private final SocketChannel channel;

    public Client(String address, Integer port) throws IOException {
        this.address = address;
        this.port = port;

        this.channel = SocketChannel.open();
        this.channel.connect(new InetSocketAddress(address, port));
    }

    public <T extends Serializable> T readObject() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        while (this.channel.read(buffer) != -1) {

            //buffer.flip(); //хз зачем нужно было, без него работает, с ним нет
            byteOut.write(buffer.array(), 0, buffer.limit());
            buffer.clear();

            byte lastByte = byteOut.toByteArray()[byteOut.size() - 1];
            if (lastByte == 1 || lastByte == 0) break;

        }

        return Serializer.deserialize(byteOut.toByteArray());
    }

    public <T extends Serializable> void writeObject(T obj) throws IOException {
        this.channel.write(ByteBuffer.wrap(Serializer.serialize(obj)));
    }

    public Integer getPort() {
        return this.port;
    }

    public String getAddress() {
        return this.address;
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    public void register(Selector selector, Integer key) throws ClosedChannelException {
        this.channel.register(selector, key);
    }

    public void register(Selector selector, Integer key, Object attachment) throws ClosedChannelException {
        this.channel.register(selector, key, attachment);
    }
}