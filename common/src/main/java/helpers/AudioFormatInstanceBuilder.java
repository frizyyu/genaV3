package helpers;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioFormatInstanceBuilder {
    private final AudioFormat audioFormat;
    private static final AudioFormatInstanceBuilder INSTANCE;

    static {
        try {
            INSTANCE = new AudioFormatInstanceBuilder();
        } catch (IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private AudioFormatInstanceBuilder() throws IOException, LineUnavailableException {
        AudioFormatProperties aConstants = new AudioFormatProperties();
        AudioFormat.Encoding encoding = aConstants.ENCODING;
        float rate = aConstants.RATE;
        int channels = aConstants.CHANNELS;
        int sampleSize = aConstants.SAMPLE_SIZE;
        boolean bigEndian = aConstants.BIG_ENDIAN;
        audioFormat = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);

    }
    //com.sun.media.sound.PortMixer$PortMixerPort@3e289415

    public static AudioFormatInstanceBuilder getInstance(){
        return INSTANCE;
    }

    public AudioFormat getAudioFormat(){
        return audioFormat;
    }
}
