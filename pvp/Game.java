package pvp;

public class Game {

    public static void main(String[] args) {
//        BgmPlayer bgm = new BgmPlayer("bgm.wav");
//        bgm.setDaemon(true);
//        bgm.start();

        GameUI gameUI = new GameUI();

        gameUI.startButton.addActionListener(e -> {
            gameUI.showGamePanel();
            StartThread startThread = new StartThread(gameUI);
            startThread.start();
        });

        gameUI.loadButton.addActionListener(e -> {
            gameUI.showGamePanel();
            LoadThread loadThread = new LoadThread(gameUI);
            loadThread.start();
        });

        gameUI.multiButton.addActionListener(e -> {
            gameUI.showGamePanel();
            MultiThread multiThread = new MultiThread(gameUI);
            multiThread.start();
        });

//        gameUI.multiButton.addActionLister(e -> {
//            gameUI.showGamePanel();
//        });

    }
}



