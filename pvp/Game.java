package pvp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {

    public static void main(String[] args) {
        BgmPlayer bgm = new BgmPlayer("bgm.wav");
        bgm.setDaemon(true);
        bgm.start();

        GameUI gameUI = new GameUI();

            gameUI.startButton.addActionListener(e -> {
                gameUI.showGamePanel();
                StartThread startThread = new StartThread(gameUI);
                startThread.start();
            });

            gameUI.loadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameUI.showGamePanel();
                    LoadThread loadThread = new LoadThread(gameUI);
                    loadThread.start();
                }
            });

    }
}



