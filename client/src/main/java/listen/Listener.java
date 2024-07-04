package listen;

import helpers.AudioFormatInstanceBuilder;
import helpers.AudioFormatProperties;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class Listener {
    public byte[] listen() throws IOException {

        AudioFormat format = buildAudioFormatInstance();

        SoundRecorder soundRecorder = new SoundRecorder();
        soundRecorder.build(format);

        System.out.println("Start recording ....");

        return soundRecorder.getAudioInputStream().readAllBytes();
    }

    public static AudioFormat buildAudioFormatInstance() {
        return AudioFormatInstanceBuilder.getInstance().getAudioFormat();
    }
}