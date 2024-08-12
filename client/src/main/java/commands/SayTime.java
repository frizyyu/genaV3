package commands;

public class SayTime extends Command{
    public SayTime(String commandname, String[] args, String desc) {
        super(commandname, args, desc);
    }

    @Override
    public void execute(String[] args) {
        System.out.println("тут время короче");
    }
}
