package helpers;

import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static final ConfigReader INSTANCE;

    static {
        try {
            INSTANCE = new ConfigReader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigReader() throws IOException {
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
    }

    public static ConfigReader getInstance(){
        return INSTANCE;
    }

    public String getInfoFromConfig(String key){
        return properties.getProperty(key);
    }
}
