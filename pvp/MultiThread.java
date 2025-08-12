package pvp;

import pvp.network.Client;

public class MultiThread extends Thread {
    private GameUI gameUI;

    MultiThread(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    @Override
    public void run() {
        Client client = new Client(gameUI);
        client.start();
    }
}
