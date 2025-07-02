package pvp;

public class Game {

    public static void main(String[] args) {
        BgmPlayer bgm = new BgmPlayer("bgm.wav");
        bgm.setDaemon(true);
        bgm.start();

        GameUI gameUI = new GameUI();
        GameLogic gameLogic = new GameLogic(gameUI);
        gameLogic.gameLogic();
    }

}


