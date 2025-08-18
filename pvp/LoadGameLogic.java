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
    private int whatPlayer;
    private int whatEnemy;
    boolean playerTurn;
    private int playerHp;
    private int enemyHp;
    private int loadedPlayerHp;
    private int loadedEnemyHp;
    private int loadedTurnCount;
    private boolean loadedPlayerTurn;
    private int loadedWhatPlayer;
    private int loadedWhatEnemy;
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

    private void loadGame(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] parts = line.split(",");

            loadedPlayerHp = Integer.parseInt(parts[0]);
            loadedEnemyHp = Integer.parseInt(parts[1]);
            loadedTurnCount = Integer.parseInt(parts[2]);
            loadedPlayerTurn = Boolean.parseBoolean(parts[3]);
            loadedWhatPlayer = Integer.parseInt(parts[4]);
            loadedWhatEnemy = Integer.parseInt(parts[5]);

            reader.close();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void userTurn(AtomicBoolean skillChoice) {
        if (player.alive()) {
            gameUI.append("\n플레이어가 공격할 차례입니다!");
            gameUI.append("공격 방식을 정해주세요");
            for (int i = 0; i < player.serverSkills.length; i++) {
                gameUI.append((i + 1) + ". " + player.serverSkills[i].name +
                        " 데미지:" + player.serverSkills[i].damage +
                        " 명중률:" + player.serverSkills[i].accuracy + "%");
            }
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());

            gameUI.submitButton.addActionListener(e -> {
                String input = gameUI.getInputText().getText().trim();
                try {
                    int val = Integer.parseInt(input);
                    if (val >= 1 && val <= 3) {
                        choice = val;
                        gameUI.append("\n선택된 스킬 번호: " + choice);
                        gameUI.getInputText().setText("");
                        if (choice == 1) {
                            player.ultimate(enemy);
                        } else if (choice == 2) {
                            player.mainSkill(enemy);
                        } else {
                            player.normalSkill(enemy);
                        }
                        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                        gameUI.playerHpBar.setValue(player.hp);
                        gameUI.enemyHpBar.setValue(enemy.hp);
                        skillChoice.set(true);
                    } else {
                        gameUI.append("1~3 중에서 입력해주세요.");
                        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("숫자를 입력해주세요.");
                    gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                }
            });
        } else {
            gameUI.append("\n플레이어 패배!");
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            Timer timer = new Timer(2000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        }

        // 선택될 때까지 대기
        while (!skillChoice.get()) {
            System.out.println("5초마다 출력됩니다!");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void enemyTurn() {

        if (enemy.alive()) {
            gameUI.append("\n적이 공격할 차례입니다!");
            int enemyAttack = random.nextInt(3);
            if (enemyAttack == 0) {
                enemy.ultimate(player);
            } else if (enemyAttack == 1) {
                enemy.mainSkill(player);
            } else {
                enemy.normalSkill(player);
            }
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            gameUI.playerHpBar.setValue(player.hp);
            gameUI.enemyHpBar.setValue(enemy.hp);
        } else {
            gameUI.append("\n플레이어 승리!");
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            Timer timer = new Timer(2000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void checkVictory() {
        if (player.hp > enemy.hp)  {
            gameUI.append("\n플레이어 승리!");
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            Timer timer = new Timer(5000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        } else {
            gameUI.append("\n플레이어 패배!");
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            Timer timer = new Timer(5000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
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
                                whatPlayer + "," +
                                whatEnemy
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
        if (loadedWhatPlayer == 0){
            player = new Mage("player", gameUI);
        } else if (loadedWhatPlayer == 1){
            player = new Gunner("player", gameUI);
        } else if (loadedWhatPlayer == 2){
            player = new Priest("player", gameUI);
        } else {
            player = new SwordMaster("player", gameUI);
        }

        if (loadedWhatEnemy == 0){
            enemy = new Mage("enemy", gameUI);
        } else if (loadedWhatEnemy == 1){
            enemy = new Gunner("enemy", gameUI);
        } else if (loadedWhatEnemy == 2){
            enemy = new Priest("enemy", gameUI);
        } else {
            enemy = new SwordMaster("enemy", gameUI);
        }

        String enemyCha;
        if (enemy.hp == 150) {
            enemyCha = "마법사";
        } else if (enemy.hp == 170) {
            enemyCha = "거너";
        } else if (enemy.hp == 225) {
            enemyCha = "프리스트";
        } else {
            enemyCha = "소드마스터";
        }
        gameUI.append("적의 캐릭터는" + enemyCha + "입니다!");

        String playerCha;
        if (player.hp == 150) {
            playerCha = "마법사";
        } else if (player.hp == 170) {
            playerCha = "거너";
        } else if (player.hp == 225) {
            playerCha = "프리스트";
        } else {
            playerCha = "소드마스터";
        }
        gameUI.append("나의 캐릭터는" + playerCha + "입니다!");

        //저장된 체력으로 초기화
        player.hp = loadedPlayerHp;
        enemy.hp = loadedEnemyHp;

        //최대 체력 설정
        if (loadedWhatPlayer == 0){
            maximumPlayerHp = 150;
        } else if (loadedWhatPlayer == 1){
            maximumPlayerHp = 170;
        } else if (loadedWhatPlayer == 2){
            maximumPlayerHp = 225;
        } else {
            maximumPlayerHp = 180;
        }

        if (loadedWhatEnemy == 0){
            maximumEnemyHP = 150;
        } else if (loadedWhatEnemy == 1){
            maximumEnemyHP = 170;
        } else if (loadedWhatEnemy == 2){
            maximumEnemyHP = 225;
        } else {
            maximumEnemyHP = 180;
        }

        //체력bar 설정
        gameUI.playerHpBar.setMaximum(maximumPlayerHp);
        gameUI.enemyHpBar.setMaximum(maximumEnemyHP);
        gameUI.playerHpBar.setValue(loadedPlayerHp);
        gameUI.enemyHpBar.setValue(loadedEnemyHp);

        playerTurn = loadedPlayerTurn;
        gameUI.append(playerTurn ? "\n플레이어가 선공입니다!" : "\n적이 선공입니다!");

        if (playerTurn) {
            saveGame();
            for (int i = 0; i < loadedTurnCount; i++) {
                userTurn(skillChoice[i]);
                enemyTurn();
                turnCount = loadedTurnCount - i;
                gameUI.turnLabel.setText(turnCount + "턴 남았습니다!");
                playerHp = player.hp;
                enemyHp = enemy.hp;
            }
            checkVictory();
        } else {
            saveGame();
            for (int i = 0; i < 20; i++) {
                enemyTurn();
                userTurn(skillChoice[i]);
                turnCount = loadedTurnCount - i;
                gameUI.turnLabel.setText(turnCount + "턴 남았습니다!");
                playerHp = player.hp;
                enemyHp = enemy.hp;
            }
            checkVictory();
        }

    }
}