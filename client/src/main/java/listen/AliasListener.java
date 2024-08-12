package listen;

import helpers.ConfigReader;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AliasListener {
    private static final LinkedList ACTIVATION_WORDS = new LinkedList();
    private static final int BUFFER_SIZE = 2048;
    private final Recognizer recognizer;
    private final TargetDataLine microphone;

    public AliasListener() throws LineUnavailableException, IOException {
        ACTIVATION_WORDS.add("гена");
        ACTIVATION_WORDS.add("ген");
        ACTIVATION_WORDS.add("генка");
        ACTIVATION_WORDS.add("геннадий");
        System.out.println("инициализация модели Vosk...");
        LibVosk.setLogLevel(LogLevel.DEBUG); // Уровень логирования
        Model model = new Model(ConfigReader.getInstance().getInfoFromConfig("aliasAiModel")); // Укажите путь к модели Vosk
        recognizer = new Recognizer(model, 16000);
        microphone = getMicrophone();
    }

    public boolean hear() {
        String[] splitPhrase;
        recognizer.reset();
        System.out.println("Начало распознавания...");

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = microphone.read(buffer, 0, buffer.length)) != -1) {
            if (bytesRead > 0) {
                boolean accepted = recognizer.acceptWaveForm(buffer, bytesRead);
                if (accepted) {
                    splitPhrase = recognizer.getResult().replace("\"", "").replace("\n}", "").split(" ");
                } else {
                    splitPhrase = recognizer.getPartialResult().replace("\"", "").replace("\n}", "").split(" ");

                }
                if (ACTIVATION_WORDS.contains(splitPhrase[splitPhrase.length - 1])) {
                    System.out.println("Услышал");
                    return true;
                }
            }
        }

        return false;
    }


    private static TargetDataLine getMicrophone() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        return new SoundRecorder().getTargetDataLineForRecord(format);
    }

    public void closeMicro(){
        microphone.stop();
        microphone.close();
    }
    public void openMicro() throws LineUnavailableException {
        microphone.open();
        microphone.start();
    }
}