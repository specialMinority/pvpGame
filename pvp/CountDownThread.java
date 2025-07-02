package pvp;

import java.util.Timer;
import java.util.TimerTask;

class CountDownThread extends Thread {

    public Timer timer;

    @Override
    public void run() {

        timer = new Timer();

        for (int i = 0; i < 11; i++) {
            final int countDown = i;
            timer.schedule(new TimerTask() {
                public void run() {
                    int remaining = 10 - countDown;
                    if (remaining > 0) {
                        System.out.println("남은시간: " + remaining + "초");
                    } else {
                        timer.cancel();
                    }
                }
            }, countDown * 1000);
        }
    }
}
