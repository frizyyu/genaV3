package TextToSpeech;

import com.sun.tools.javac.Main;
import helpers.AudioFormatInstanceBuilder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Speecher {
    private Clip clip = null;
    public AudioInputStream byteToStream(byte[] input){
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

    private AudioInputStream changeSpeed(AudioInputStream stream, float speed, AudioFormat format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        byte[] audioBytes = baos.toByteArray();

        int newSampleRate = (int) (format.getSampleRate() / speed);
        AudioFormat newFormat = new AudioFormat(format.getEncoding(), newSampleRate, format.getSampleSizeInBits(),
                format.getChannels(), format.getFrameSize(), newSampleRate, format.isBigEndian());

        return new AudioInputStream(new ByteArrayInputStream(audioBytes), newFormat, audioBytes.length / newFormat.getFrameSize());
    }

    public void stop(){
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}