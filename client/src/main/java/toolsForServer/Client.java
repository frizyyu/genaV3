package toolsForServer;

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

    public <T extends Serializable> T readObject() throws IOException, ClassNotFoundException {
        System.out.println("QWEQWEQWE");
        ByteBuffer buffer = ByteBuffer.allocate(8192); // Уменьшенный буфер для чтения произвольной длины данных
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = this.channel.read(buffer)) != -1) {
            //buffer.flip(); // Переключение буфера в режим чтения
            byteOut.write(buffer.array(), 0, buffer.limit()); // Запись данных из буфера в ByteArrayOutputStream
            buffer.clear(); // Очистка буфера для следующего чтения

            byte[] byteArray = byteOut.toByteArray();
            if (byteArray.length > 0) {
                byte lastByte = byteArray[byteArray.length - 1];
                if (lastByte == 1 || lastByte == 0) {
                    break;
                }
            }
        }

        return Serializer.deserialize(byteOut.toByteArray());
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