package serverHelpers;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WaveDataUtil {
    public String saveToFile(String name, AudioFileFormat.Type fileType, AudioInputStream audioInputStream) {
        System.out.println("Saving...");
        if (null == name || null == fileType || audioInputStream == null) {
            System.out.println("null");
            return null;
        }
        File myFile = new File(name + "." + fileType.getExtension());
        try {
            audioInputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int i = 0;
        /*while (myFile.exists()) {
            String temp = "" + i + myFile.getName();
            myFile = new File(temp);
        }*/
        try {
            AudioSystem.write(audioInputStream, fileType, myFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return myFile.getAbsolutePath();
    }
}
