import exeption.ClientInterrupt;
import helpers.*;
import pythonJavaCommunication.CallPython;
import serverTools.Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import serverHelpers.WaveDataUtil;

public class Main {
    private static Selector selector;
    private static final String MODEL = "base";

    public static void main(String[] args) throws IOException {
        selector = Selector.open();
        Server server = new Server(Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("port")));
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started");
        while (true) {
            try {
                selectLoop();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("ERROR");
            }
        }
    }

    private static void selectLoop() throws IOException, ClassNotFoundException {
        if (selector.selectNow() == 0) return;

        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isAcceptable()) doAccept(key);
            if (key.isReadable()) doRead(key);
            if (key.isWritable()) doWrite(key);

            keyIterator.remove();
        }
    }

    private static void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        SocketChannel client = server.accept();
        client.configureBlocking(false);

        client.register(selector, SelectionKey.OP_READ);

        System.out.println("New client: " + client.getRemoteAddress());
    }

    private static void doRead(SelectionKey key) throws IOException, ClassNotFoundException {
        try {
            System.out.println("Read0");
            SocketChannel client = (SocketChannel) key.channel();

            Request request = getRequest(client);
            String data = getTextFromAudio(converToAudioInputStream(request.voiceCommand()));
            Response response = new Response(data); //заглушка
            //Response response = getResponse(request); //исполнение команды

            client.register(selector, SelectionKey.OP_WRITE, response);

            System.out.println("Read");

            //LOGGER.info("Client (" + client.getRemoteAddress() + ") send request");
        } catch (SocketException e) {
            throw new ClientInterrupt((SocketChannel) key.channel());
        }
    }

    private static void doWrite(SelectionKey key) throws IOException {

        try {
            SocketChannel client = (SocketChannel) key.channel();
            Response response = (Response) key.attachment();

            client.write(ByteBuffer.wrap(Serializer.serialize(response)));

            client.register(selector, SelectionKey.OP_READ);
            System.out.println("Write");

            //LOGGER.info("Server send response to client (" + client.getRemoteAddress() + ")");
        } catch (SocketException e) {
            throw new ClientInterrupt((SocketChannel) key.channel());
        }
    }

    private static Request getRequest(SocketChannel client) throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(8192); // Меньший буфер для многократного чтения
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;

        while ((bytesRead = client.read(buffer)) > 0) {
            buffer.flip();
            byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
            buffer.clear();
        }

        if (bytesRead == -1) {
            throw new ConnectException("Connection with a client (" + client.getRemoteAddress() + ") was closed");
        }

        byte[] data = byteArrayOutputStream.toByteArray();

        System.out.println("Total data length: " + data.length);
        System.out.println("Total data content: " + Arrays.toString(data));

        return Serializer.deserialize(data);
    }

    private static String getTextFromAudio(AudioInputStream inputStream) throws IOException {
        String resData;
        WaveDataUtil wd = new WaveDataUtil();
        String fileName = String.format("/%s", ConfigReader.getInstance().getInfoFromConfig("fileName"));
        String filePath = wd.saveToFile(fileName, AudioFileFormat.Type.WAVE, inputStream);
        CallPython cp = new CallPython();
        resData = cp.call(ConfigReader.getInstance().getInfoFromConfig("pythonCommandAiScript"), new String[]{MODEL, filePath});
        System.out.println(resData);
        return resData;
    }

    private static AudioInputStream converToAudioInputStream(byte[] input){
        AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(input),
                AudioFormatInstanceBuilder.getInstance().getAudioFormat(),
                input.length
        );
        return stream;
    }

    /*private static Response getResponse(Request request) {
        ServerCommand command = (ServerCommand) commandManager.getCommandList().get(request.command());
        return command.serverExecute(request.arguments(), request.routeReader());
    }*/
}
