package pvp;

import pvp.character.Character;
import pvp.character.Gunner;
import pvp.character.Mage;
import pvp.character.Priest;
import pvp.character.SwordMaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Timer;

public class LoadGameLogic {
    private final GameUI gameUI;
    private volatile int choice = -1;
    private final Random random = new Random();
    private final AtomicBoolean[] skillChoice = new AtomicBoolean[20];
    private pvp.character.Character player;
    private Character enemy;
    private int turnCount;
    private int playerTypeIndex;
    private int enemyTypeIndex;
    boolean playerTurn;
    private int playerHp;
    private int enemyHp;
    private int loadedPlayerHp;
    private int loadedEnemyHp;
    private int loadedTurnCount;
    private boolean loadedPlayerTurn;
    private int loadedPlayerTypeIndex;
    private int loadedEnemyTypeIndex;
    private int maximumPlayerHp;
    private int maximumEnemyHP;

    public LoadGameLogic(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void loadGameLogic() {
        for (int i = 0; i < 20; i++) {
            skillChoice[i] = new AtomicBoolean(false);
        }
        startBattle();
    }

    private java.awt.event.ActionListener currentListener;

    private void setSubmitListener(java.awt.event.ActionListener listener) {
        if (currentListener != null) {
            gameUI.submitButton.removeActionListener(currentListener);
        }
        currentListener = listener;
        gameUI.submitButton.addActionListener(currentListener);
    }
    
    //todo GameLogic LoadLogic 합치기
    private void loadGame(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            String[] parts = line.split(",");

            loadedPlayerHp = Integer.parseInt(parts[0]);
            loadedEnemyHp = Integer.parseInt(parts[1]);
            loadedTurnCount = Integer.parseInt(parts[2]);
            loadedPlayerTurn = Boolean.parseBoolean(parts[3]);
            loadedPlayerTypeIndex = Integer.parseInt(parts[4]);
            loadedEnemyTypeIndex = Integer.parseInt(parts[5]);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        gameUI.saveButton.addActionListener(e -> {
            try {
                FileWriter fileWriter;
                fileWriter = new FileWriter("save.txt");
                fileWriter.write(
                        playerHp + "," +
                                enemyHp + "," +
                                turnCount + "," +
                                playerTurn + "," +
                                playerTypeIndex + "," +
                                enemyTypeIndex
                );
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // 저장 완료 후 종료
            Timer timer = new Timer(2000, e2 -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void startBattle() {
        //불러오기
        loadGame("save.txt");

        // 캐릭터 생성
        switch (loadedPlayerTypeIndex) {
            case 0 -> player = new Mage("player", gameUI);
            case 1 -> player = new Gunner("player", gameUI);
            case 2 -> player = new Priest("player", gameUI);
            default -> player = new SwordMaster("player", gameUI);
        }

        switch (loadedEnemyTypeIndex) {
            case 0 -> enemy = new Mage("enemy", gameUI);
            case 1 -> enemy = new Gunner("enemy", gameUI);
            case 2 -> enemy = new Priest("enemy", gameUI);
            default -> enemy = new SwordMaster("enemy", gameUI);
        }

        String enemyCha = switch (enemy.type) {
            case MAGE -> "メイジ";
            case GUNNER -> "ガンナー";
            case PRIEST -> "プリースト";
            default -> "ソードマスター";
        };
        gameUI.append("敵のキャラクターは" + enemyCha + "です！");

        String playerCha = switch (player.type) {
            case MAGE -> "メイジ";
            case GUNNER -> "ガンナー";
            case PRIEST -> "プリースト";
            default -> "ソードマスター";
        };
        gameUI.append("あなたのキャラクターは" + playerCha + "です！");

        //저장된 체력으로 초기화
        player.hp = loadedPlayerHp;
        enemy.hp = loadedEnemyHp;

        //최대 체력 설정
        switch (loadedPlayerTypeIndex) {
            case 0 -> maximumPlayerHp = 150;
            case 1 -> maximumPlayerHp = 170;
            case 2 -> maximumPlayerHp = 225;
            default -> maximumPlayerHp = 180;
        }

        switch (loadedEnemyTypeIndex) {
            case 0 -> maximumEnemyHP = 150;
            case 1 -> maximumEnemyHP = 170;
            case 2 -> maximumEnemyHP = 225;
            default -> maximumEnemyHP = 180;
        }

        //체력bar 설정
        gameUI.playerHpBar.setMaximum(player.maxHp);
        gameUI.enemyHpBar.setMaximum(enemy.maxHp);
        gameUI.playerHpBar.setValue(player.hp);
        gameUI.enemyHpBar.setValue(enemy.hp);

        //마나bar 설정
        gameUI.playerMpBar.setMaximum(player.maxMp);
        gameUI.enemyMpBar.setMaximum(enemy.maxMp);
        gameUI.playerMpBar.setValue(player.mp);
        gameUI.enemyMpBar.setValue(enemy.mp);

        playerTurn = loadedPlayerTurn;
        gameUI.append(playerTurn ? "\nプレイヤーが先攻です！" : "\n敵が先攻です！");
        
        pvp.logic.BattleEngine engine = new pvp.logic.BattleEngine(gameUI, player, enemy);

        if (playerTurn) {
            saveGame();
            for (int i = 0; i < loadedTurnCount; i++) {
                engine.processUserTurn(skillChoice[i]);
                engine.processEnemyTurn();
                turnCount = i + 1;
                gameUI.turnLabel.setText(turnCount + "ターン経過しました！");
                playerHp = player.hp;
                enemyHp = enemy.hp;
            }
            engine.checkVictory();
        } else {
            saveGame();
            for (int i = 0; i < 20; i++) {
                engine.processEnemyTurn();
                engine.processUserTurn(skillChoice[i]);
                turnCount = i + 1;
                gameUI.turnLabel.setText(turnCount + "ターン経過しました！");
                playerHp = player.hp;
                enemyHp = enemy.hp;
            }
            engine.checkVictory();
        }

    }
}