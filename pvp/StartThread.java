package pvp;

public class StartThread extends Thread{
    GameUI gameUI;

    public StartThread(GameUI gameUI){
        this.gameUI = gameUI;
    }
    @Override
    public void run() {
        GameLogic gameLogic = new GameLogic(gameUI);
        gameLogic.gameLogic();
    }

}
