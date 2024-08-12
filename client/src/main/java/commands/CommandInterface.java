package commands;

public interface CommandInterface {
    void execute(String[] args); //тут аргументы
    String getName();
    String getDesc();

}
