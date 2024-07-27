import helpers.*;
import listen.*;
import pythonJavaCommunication.CallPython;
import toolsForServer.Client;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        //подключение к серверу
        Client client = new Client(ConfigReader.getInstance().getInfoFromConfig("serverAddress"), Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("serverPort")));

        Listener listener = new Listener();
        CallPython cp = new CallPython();

        //отдельный поток нужен, чтобы во время ответа от ассистента можно было его прервать и послушать новую команду
        Thread hearingThread = new Thread(() ->{
            while (true) {
                try {
                    if (cp.call(ConfigReader.getInstance().getInfoFromConfig("pythonAliasAiScript"), new String[]{ConfigReader.getInstance().getInfoFromConfig("microphoneId"), ConfigReader.getInstance().getInfoFromConfig("aliasAIToken")}).equals("Hear")) {
                        byte[] userCommand = listener.listen();
                        //результат listen нужно передавать на сервер для расшифровки
                        client.writeObject(new Request(userCommand));

                        Response response = client.readObject();
                        System.out.println("ASDASD");
                        System.out.println(response.textCommand());
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        hearingThread.start();

    }
}
