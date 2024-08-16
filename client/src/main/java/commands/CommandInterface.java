package commands;

public interface CommandInterface {
    void execute(String[] args); //тут аргументы
    String getOutput();
    String getName();
    String getDesc();

}
