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
    public void executeCommand(String command, String[] args){
        try {
            commandMap.get(command).execute(args);
        } catch (NullPointerException e){
            System.out.println("Неизвестная команда");
        }
    }
}
