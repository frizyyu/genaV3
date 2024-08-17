package toolsForServer;

import helpers.Request;
import helpers.Response;
import helpers.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Client {
    private final String address;
    private final Integer port;
    private final int DELAY = 5000;
    private SocketChannel channel;

    public Client(String address, Integer port) throws IOException {
        this.address = address;
        this.port = port;
        connect();
    }

    private void connect() throws IOException {
        this.channel = SocketChannel.open();
        try {
            this.channel.connect(new InetSocketAddress(address, port));
        } catch (ConnectException ignored){}
    }

    public <T extends Serializable> T readObject() throws IOException, ClassNotFoundException, InterruptedException {
        //работает только такой вариант
        //сначала получаем длину данных, которые сервер хочет отправить, а потом уже принимаем их
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);

        // Чтение длины сообщения
        while (lengthBuffer.hasRemaining()) {
            if (this.channel.read(lengthBuffer) == -1) {
                throw new IOException("Connection closed before length was read.");
            }
        }
        lengthBuffer.flip();
        int messageLength = lengthBuffer.getInt();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        int remainingBytes = messageLength;
        int bytesRead;
        while (remainingBytes > 0 && (bytesRead = this.channel.read(buffer)) != -1) {
            if (bytesRead > 0) {
                buffer.flip();
                byteOut.write(buffer.array(), 0, bytesRead);
                remainingBytes -= bytesRead;
                buffer.clear();
            }
        }

        if (remainingBytes > 0) {
            throw new IOException("Incomplete data received.");
        }

        byte[] data = byteOut.toByteArray();
        System.out.println("Size of received data: " + data.length);
        return Serializer.deserialize(data);
    }







    public <T extends Serializable> void writeObject(T obj) throws IOException {
        byte[] serializedData = Serializer.serialize(obj);
        ByteBuffer buffer = ByteBuffer.wrap(serializedData);
        while (buffer.hasRemaining()) {
            this.channel.write(buffer);
        }
    }

    public boolean isConnected() {
        try {
            writeObject(null);
            return true;
        } catch (IOException e){
            return false;
        }
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