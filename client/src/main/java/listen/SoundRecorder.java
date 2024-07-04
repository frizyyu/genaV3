package listen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SoundRecorder implements Runnable {
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    public Thread thread;
    private double duration;
    byte[] data;
    int numBytesRead;
    int counterBeforeStop = 0;
    private final int WAITBEFORESTOP = 20; //можно менять для обрезания хвоста после замолкания

    //для проверки на тишину
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] dataSilence;
    short[] shorts;
    long startSilence = 0;
    boolean stopRecording = false;
    private final double RMS = 0.007; //можно менять, если понадобится, влияет на чувствительность микрофона

    public SoundRecorder() {
        super();
    }

    public SoundRecorder(AudioFormat format) {
        this.format = format;
    }

    public SoundRecorder build(AudioFormat format) {
        this.format = format;
        return this;
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Capture Microphone");
        thread.start();
    }
    public void stop() {
        thread = null;
    }

    @Override
    public void run() {
        duration = 0;

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(); final TargetDataLine line = getTargetDataLineForRecord();) {
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            buildByteOutputStream(out, line, frameSizeInBytes, bufferLengthInBytes);
            this.audioInputStream = new AudioInputStream(line);
            setAudioInputStream(convertToAudioIStream(out, frameSizeInBytes));
            audioInputStream.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public void buildByteOutputStream(final ByteArrayOutputStream out, final TargetDataLine line, int frameSizeInBytes, final int bufferLengthInBytes) throws IOException, InterruptedException {
        data = new byte[bufferLengthInBytes];
        line.start();

        dataSilence = new byte[line.getBufferSize() / 5];
        shorts = new short[data.length / 2];

        while (thread != null) {
            if (((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) || counterBeforeStop > WAITBEFORESTOP) {
                stop();
            }

            //проверка на тишину. если находим молчание, то останавливаем прослушивание
            //добавление в out(будущий аудиофайл) происходит, если не слышно тишины. При фразе "<тишина> который <тишина> час <тишина>" добавится только "который час"
            if (searchForSilence()) {
                counterBeforeStop += 1;
                if (counterBeforeStop < 7) //сделано, чтобы при резком молчании посреди фразы сильно не обрезать конец (из-за чувствительности микро может обрезать с, з и тд)
                    out.write(data, 0, numBytesRead);
            }

            else {
                counterBeforeStop = 0;
                out.write(data, 0, numBytesRead);
            }
        }
    }

    private boolean searchForSilence(){
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        double rms = 0;
        for (short aShort : shorts) {
            double normal = aShort / 32768f;
            rms += normal * normal;
        }
        rms = Math.sqrt(rms / shorts.length);
        System.out.println("rms and stopRecording: " + rms + stopRecording);
        if (rms < RMS) { //проверка чувствительности микро
            long now = System.currentTimeMillis();
            if (!stopRecording) {
                startSilence = now;
            }
            stopRecording = true;
        } else {
            stopRecording = false;
        }
        return stopRecording;
    }

    private void setAudioInputStream(AudioInputStream aStream) {
        this.audioInputStream = aStream;
    }

    public AudioInputStream convertToAudioIStream(final ByteArrayOutputStream out, int frameSizeInBytes) {
        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
        long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;
        System.out.println("Recorded duration in seconds:" + duration);
        return audioStream;
    }

    public TargetDataLine getTargetDataLineForRecord() {
        TargetDataLine line;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            return null;
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (final Exception ex) {
            return null;
        }
        return line;
    }

    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void setFormat(AudioFormat format) {
        this.format = format;
    }

    public Thread getThread() {
        return thread;
    }

    public double getDuration() {
        return duration;
    }
}