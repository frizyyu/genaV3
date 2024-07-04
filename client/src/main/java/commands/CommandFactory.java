package commands;

import java.util.HashMap;

public class CommandFactory {
    private static final HashMap<String, ?> commandMap = new HashMap<>();
    static {
        commandMap.put("команда", null); //так для каждого, вместо нул придумать, чё пихнуть можно
    }
}
