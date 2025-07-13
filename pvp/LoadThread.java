package pvp;

public class LoadThread extends Thread{
    GameUI gameUI;

    public LoadThread(GameUI gameUI){
        this.gameUI = gameUI;
    }

    @Override
    public void run(){
        LoadGameLogic loadGameLogic = new LoadGameLogic(gameUI);
        loadGameLogic.loadGameLogic();
    }

}
