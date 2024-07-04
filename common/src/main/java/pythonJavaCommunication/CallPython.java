package pythonJavaCommunication;
import helpers.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class CallPython {

     public String call(String filePath, String[] args) throws IOException { //сделать 1 файл для вызова программ на питоне. передавать как аргумент файл .py, аргументы в виде String[]

        //выбор аргументов для передачи в Python-скрипт
        String pythonInterpreter = ConfigReader.getInstance().getInfoFromConfig("pythonInterpreter");
        //ConfigReader.getInstance().getInfoFromConfig("pythonAliasAiScript")
         // {ConfigReader.getInstance().getInfoFromConfig("microphoneId"), ConfigReader.getInstance().getInfoFromConfig("aliasAIToken")}



        // Команда для запуска Python-скрипта
        String[] command = new String[2 + args.length];
        command[0] = pythonInterpreter;
        command[1] = filePath;
        System.arraycopy(args, 0, command, 2, args.length);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Чтение вывода Python-скрипта
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}