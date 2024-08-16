package TextToSpeech;

import com.sun.tools.javac.Main;
import helpers.AudioFormatInstanceBuilder;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.util.Objects;

public class Speecher {
    private Clip clip = null;
    public AudioInputStream saveToWav(byte[] input){
        return new AudioInputStream(
                new ByteArrayInputStream(input),
                AudioFormatInstanceBuilder.getInstance().getAudioFormat(),
                input.length
        );
    }
    public void say(AudioInputStream stream) {
        new Thread(() -> {
            try {
                clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }

    public void stop(){
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}