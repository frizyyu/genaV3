package pythonJavaCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallPython {

    private Process process;
    private Writer writer;
    private BufferedReader reader;

    public CallPython(String pythonInterpreter, String filePath) throws IOException {
        String[] command = { pythonInterpreter, filePath };
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        this.process = pb.start();
        this.writer = new OutputStreamWriter(this.process.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));

        // Ждем подтверждения готовности от Python
        String ready = this.reader.readLine();
        if (!"READY".equals(ready)) {
            throw new IOException("Failed to start Python process");
        }
    }

    public String call(String command) throws IOException {
        System.out.println(command);
        this.writer.write(command + "\n");
        this.writer.flush();

        StringBuilder output = new StringBuilder();
        String line;

        // Чтение всех строк до тех пор, пока не будет достигнут маркер "END"
        while ((line = this.reader.readLine()) != null) {
            if (line.equals("success")) { // Замените "END" на ваш маркер окончания вывода
                break;
            }
            output.append(line).append(System.lineSeparator());
        }
        String[] out = output.toString().split("\n");

        System.out.println("-------------------------Python output-------------------------");
        System.out.println(output.toString().strip());
        System.out.println("---------------------------------------------------------------");

        return out[out.length - 1];
    }

    public void close() throws IOException {
        this.writer.write("exit\n");
        this.writer.flush();
        this.process.destroy();
        this.reader.close();
        this.writer.close();
    }

    public String getFullLogs() throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = this.reader.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }
        return String.format("""
                ----------Full python logs----------
                %s
                ------------------------------------""", output);
    }
}

/*package pythonJavaCommunication;
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

            StringBuilder output = new StringBuilder();
            String line;

            // Читаем все строки из буфера
            while ((line = in.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            // Выводим все, что было прочитано из буфера
            System.out.println(output.toString());

            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}*/