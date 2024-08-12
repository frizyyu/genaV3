package commands;

public abstract class Command implements CommandInterface{
    private final String COMMANDNAME;
    private final String[] args;
    private final String DESC;

    protected Command(String commandname, String[] args, String desc) {
        COMMANDNAME = commandname;
        this.args = args;
        DESC = desc;
    }

    public String getName(){
        return COMMANDNAME;
    }
    public String[] getArgs(){
        return args;
    }
    public String getDesc(){
        return DESC;
    }
}
