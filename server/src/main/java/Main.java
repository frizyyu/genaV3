import helpers.*;
import pythonJavaCommunication.CallPython;
import serverHelpers.WaveDataUtil;
import serverTools.Server;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;

public class Main {
    private static Selector selector;

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

            if (!key.isValid()) {
                keyIterator.remove();
                continue;
            }

            try {
                if (key.isAcceptable()) doAccept(key);
                if (key.isReadable()) doRead(key);
                if (key.isWritable()) doWrite(key);
            } catch (CancelledKeyException e) {
                keyIterator.remove();
                continue;
            }

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
            SocketChannel client = (SocketChannel) key.channel();

            Request request = getRequest(client);
            if (request != null) {
                String data = getTextFromAudio(converToAudioInputStream(request.voiceCommand()));
                Response response = getResponse(data); //исполнение команды

                client.register(selector, SelectionKey.OP_WRITE, response);

                System.out.println("Read");
            }

            //LOGGER.info("Client (" + client.getRemoteAddress() + ") send request");
        } catch (SocketException e) {
            clientInterrupt(key);
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
            clientInterrupt(key);
        }
    }

    private static void clientInterrupt(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        System.out.println("Client (" + client.getRemoteAddress() + ") interrupted. Closing connection.");

        key.cancel();
        client.close();
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

        return Serializer.deserialize(data);
    }

    private static String getTextFromAudio(AudioInputStream inputStream) throws IOException {
        String resData;
        WaveDataUtil wd = new WaveDataUtil();
        String fileName = String.format("/%s", ConfigReader.getInstance().getInfoFromConfig("fileName"));
        String filePath = wd.saveToFile(fileName, AudioFileFormat.Type.WAVE, inputStream);
        CallPython cp = new CallPython();
        resData = cp.call(ConfigReader.getInstance().getInfoFromConfig("pythonCommandAiScript"), new String[]{ConfigReader.getInstance().getInfoFromConfig("voskModelRuPath"), ConfigReader.getInstance().getInfoFromConfig("voskModelEnPath"), filePath});
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

    private static Response getResponse(String textCommand) throws IOException { //method for getting command from list with input user command, allows for variability of commands
        CallPython cp = new CallPython();
        System.out.println(textCommand);
        String pathToVectorizer = ConfigReader.getInstance().getInfoFromConfig("pathToVectorizer");
        String pathToKnnModel = ConfigReader.getInstance().getInfoFromConfig("pathToKnnModel");
        String[] res = cp.call(ConfigReader.getInstance().getInfoFromConfig("pythonChooseCommand"), new String[]{pathToVectorizer, pathToKnnModel, textCommand}).split("\\|");
        return new Response(res[0], res[1].replace("[", "").replace("]", "").split(", "));
    }
}
