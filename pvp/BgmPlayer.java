package pvp;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class BgmPlayer extends Thread {
    private final String filePath;

    public BgmPlayer(String filePath) {
        this.filePath = filePath;
    }
    @Override
    public void run() {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
            }

            clip.stop();
            clip.close();
            audioStream.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            System.out.println("BGM 재생 중 오류:" + e.getMessage());
        }
    }
}
