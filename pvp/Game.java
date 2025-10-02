package pvp;

public class Game {

    public static void main(String[] args) {
//        BgmPlayer bgm = new BgmPlayer("bgm.wav");
//        bgm.setDaemon(true);
//        bgm.start();

        GameUI gameUI = new GameUI();

        // 새 게임
        gameUI.startButton.addActionListener(e -> {
            gameUI.showGamePanel();
            gameUI.retryButton.setVisible(false);
            gameUI.exitButton.setVisible(false);
            StartThread startThread = new StartThread(gameUI);
            startThread.start();
        });

        // 불러오기
        gameUI.loadButton.addActionListener(e -> {
            gameUI.showGamePanel();
            gameUI.retryButton.setVisible(false);
            gameUI.exitButton.setVisible(false);
            LoadThread loadThread = new LoadThread(gameUI);
            loadThread.start();
        });

        // 온라인 게임
        gameUI.multiButton.addActionListener(e -> {
            gameUI.showGamePanel();
            gameUI.saveButton.setVisible(false);
            gameUI.loadButton.setVisible(false);
            MultiThread multiThread = new MultiThread(gameUI);
            multiThread.start();
        });


    }
}



