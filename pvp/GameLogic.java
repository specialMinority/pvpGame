package pvp;

import pvp.character.Character;
import pvp.character.Gunner;
import pvp.character.Mage;
import pvp.character.Priest;
import pvp.character.SwordMaster;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Timer;

public class GameLogic {
    private final GameUI gameUI;
    private volatile int choice = -1;
    private boolean characterChoice = false;
    private final Random random = new Random();
    private final AtomicBoolean[] skillChoice = new AtomicBoolean[20];
    private Character player;
    private Character enemy;
    private int turnCount;
    private int playerTypeIndex;
    private int enemyTypeIndex;
    boolean playerTurn;
    private int playerHp;
    private int enemyHp;

    public GameLogic(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void gameLogic() {
        // BGM 재생
        try {
            playBGM();
        } catch (Exception e) {
            System.out.println("BGM 재생 실패: " + e.getMessage());
        }

        for (int i = 0; i < 20; i++) {
            skillChoice[i] = new AtomicBoolean(false);
        }
        gameUI.append("セーブファイルは1つだけ作成できます。\n");
        gameUI.append("メイジ: ダメージが非常に強力ですが、命中率が低く体力が低いです。");
        gameUI.append("ガンナー: ダメージは高い方ですが、命中率がやや低く体力は低めです。");
        gameUI.append("ソードマスター: ダメージ、命中率、体力のバランスが良いです。");
        gameUI.append("プリースト: ダメージは弱いですが、命中率が高く体力が高いです。");

        // 사용자 캐릭터 선택
        gameUI.append("");
        gameUI.append("キャラクターを選んでください！");
        gameUI.append("1. メイジ  2. ガンナー  3. プリースト  4. ソードマスター");

        waitForCharacterChoice();
        startBattle();
    }

    private void playBGM() {
        try {
            java.io.File bgmFile = new java.io.File("pvp/bgm.wav");
            if (!bgmFile.exists()) {
                 System.out.println("BGM 파일이 존재하지 않습니다: " + bgmFile.getAbsolutePath());
                 return;
            }

            javax.sound.sampled.AudioInputStream audioInputStream = javax.sound.sampled.AudioSystem.getAudioInputStream(bgmFile);
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private java.awt.event.ActionListener currentListener;

    private void setSubmitListener(java.awt.event.ActionListener listener) {
        if (currentListener != null) {
            gameUI.submitButton.removeActionListener(currentListener);
        }
        currentListener = listener;
        gameUI.submitButton.addActionListener(currentListener);
    }

    private void waitForCharacterChoice() {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        setSubmitListener(e -> {
            try {
                int val = Integer.parseInt(gameUI.getInputText().getText().trim());
                if (val >= 1 && val <= 4) {
                    playerTypeIndex = val - 1; // Adjust to 0-indexed
                    gameUI.append("\n選択されたキャラクター番号: " + val);
                    gameUI.append("キャラクターを選択しました。");
                    latch.countDown();
                } else {
                    gameUI.append("1~4の数字を入力してください！");
                }
            } catch (NumberFormatException ex) {
                gameUI.append("数字を入力してください！");
            }
            gameUI.getInputText().setText(""); // Clear input field
        });

        //사용자가 캐릭터를 고를 때까지 대기
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 적 캐릭터 무작위 생성
        enemyTypeIndex = random.nextInt(4);

        // create player character
        switch (playerTypeIndex) {
            case 0 -> player = new Mage("player", gameUI);
            case 1 -> player = new Gunner("player", gameUI);
            case 2 -> player = new Priest("player", gameUI);
            default -> player = new SwordMaster("player", gameUI);
        }

        // create enemy character
        switch (enemyTypeIndex) {
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
    }

    private void saveGame() {
        gameUI.saveButton.addActionListener(e -> {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("save.txt");
                fileWriter.write(
                        player.hp + "," +
                                enemy.hp + "," +
                                turnCount + "," +
                                playerTurn + "," +
                                playerTypeIndex + "," +
                                enemyTypeIndex
                );
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (fileWriter != null) {
                 try{
                     fileWriter.close();
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
                }
            }

            // 저장 완료 후 종료
            gameUI.append("ゲームが保存されました。終了します。");
            Timer timer = new Timer(2000, e2 -> System.exit(0));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void startBattle() {
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

        playerTurn = random.nextBoolean();
        gameUI.append(playerTurn ? "\nプレイヤーが先攻です！" : "\n敵が先攻です！");

        pvp.logic.BattleEngine engine = new pvp.logic.BattleEngine(gameUI, player, enemy);

        if (playerTurn) {
            saveGame();
            for (int i = 0; i < 20; i++) {
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