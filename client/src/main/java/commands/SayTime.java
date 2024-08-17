package commands;

public class SayTime extends Command{
    private String output;
    public SayTime(String commandname, String[] args, String desc) {
        super(commandname, args, desc);
    }

    @Override
    public void execute(String[] args) {
        output = "тут будет время, например, сейчас 12:42";
    }

    @Override
    public String getOutput(){
        return output;
    }
}
