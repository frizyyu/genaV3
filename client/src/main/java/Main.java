import commands.CommandFactory;
import commands.SayTime;
import helpers.*;
import listen.*;
import pythonJavaCommunication.CallPython;
import toolsForServer.Client;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class Main {
    public static void main(String[] args) throws IOException, LineUnavailableException {
        //подключение к серверу
        AtomicReference<Client> client = new AtomicReference<>(new Client(ConfigReader.getInstance().getInfoFromConfig("serverAddress"), Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("serverPort"))));

        Listener listener = new Listener();
        CommandFactory COMMANDFACTORY = new CommandFactory();
        COMMANDFACTORY.setCommandMap(new ArrayList<>(List.of(
                new SayTime("say_time", new String[]{"time difference"}, "Скажу вам время")
        )));
        AliasListener al = new AliasListener();

        //отдельный поток нужен, чтобы во время ответа от ассистента можно было его прервать и послушать новую команду
        Thread hearingThread = new Thread(() ->{
            while (true) {
                try {
                    al.openMicro();
                    if (al.hear()) {
                        al.closeMicro();
                    //if (cp.call(ConfigReader.getInstance().getInfoFromConfig("pythonAliasAiModel"), new String[]{ConfigReader.getInstance().getInfoFromConfig("microphoneId")}).equals("Hear")) {
                        if (!client.get().isConnected()){
                            client.set(new Client(ConfigReader.getInstance().getInfoFromConfig("serverAddress"), Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("serverPort"))));
                        }

                        if (client.get().isConnected()) {
                            try {
                                byte[] userCommand = listener.listen();
                                //результат listen нужно передавать на сервер для расшифровки
                                client.get().writeObject(new Request(userCommand));

                                Response response = client.get().readObject();
                                COMMANDFACTORY.executeCommand(response.textCommand(), response.args());
                                //System.out.println(response.textCommand());
                            } catch (IOException e){
                                System.out.println("Нет дступа к серверу");
                                client.set(new Client(ConfigReader.getInstance().getInfoFromConfig("serverAddress"), Integer.parseInt(ConfigReader.getInstance().getInfoFromConfig("serverPort"))));
                            }
                        }
                        else
                            System.out.println("Нет дступа к серверу");
                    }
                    al.closeMicro();
                } catch (IOException | ClassNotFoundException | InterruptedException | LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        hearingThread.start();

    }
}
