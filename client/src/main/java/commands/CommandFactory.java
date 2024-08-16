package commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandFactory {
    private final LinkedHashMap<String, Command> commandMap;
    public CommandFactory() {
        commandMap = new LinkedHashMap<>();
    }
    public void setCommandMap(ArrayList<Command> commands){
        commands.forEach(command -> commandMap.put(command.getName(), command));
    }
    public String executeCommand(String commandName, String[] args){
        try {
            Command command = commandMap.get(commandName);
            command.execute(args);
            return command.getOutput();
        } catch (NullPointerException e){
            //воспроизведение аудио с предзаписанным текстом, вдруг не будет интернета, либо нет
            return "Неизвестная команда";
        }
    }
}
