package pvp.logic;

import pvp.GameUI;
import pvp.character.Character;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles the core battle logic for the PVP Game.
 * Manages turns, damage processing, and victory conditions to avoid code duplication
 * between GameLogic (New Game) and LoadGameLogic (Loaded Game).
 */
public class BattleEngine {
    private final GameUI gameUI;
    private final Character player;
    private final Character enemy;
    private final Random random = new Random();
    private ActionListener currentListener;
    private int choice = -1;

    public BattleEngine(GameUI gameUI, Character player, Character enemy) {
        this.gameUI = gameUI;
        this.player = player;
        this.enemy = enemy;
    }

    private void setSubmitListener(ActionListener listener) {
        if (currentListener != null) {
            gameUI.submitButton.removeActionListener(currentListener);
        }
        currentListener = listener;
        gameUI.submitButton.addActionListener(currentListener);
    }

    public void processUserTurn(AtomicBoolean skillChoice) {
        if (player.alive()) {
            gameUI.append("\nプレイヤーの攻撃ターンです！");
            gameUI.append("攻撃方法を選んでください");
            for (int i = 0; i < player.serverSkills.length; i++) {
                gameUI.append((i + 1) + ". " + player.serverSkills[i].name +
                        " ダメージ:" + player.serverSkills[i].damage +
                        " 命中率:" + player.serverSkills[i].accuracy + "%");
            }
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());

            CountDownLatch latch = new CountDownLatch(1);

            setSubmitListener(e -> {
                String input = gameUI.getInputText().getText().trim();
                try {
                    int val = Integer.parseInt(input);
                    if (val >= 1 && val <= 3) {
                        choice = val;
                        gameUI.append("\n選択されたスキル番号: " + choice);
                        gameUI.getInputText().setText("");

                        int skillIndex = choice - 1; // Convert 1-indexed choice to 0-indexed array index
                        pvp.character.Skill selectedSkill = player.serverSkills[skillIndex];

                        // MP 부족 체크
                        if (player.mp < selectedSkill.mpCost) {
                            gameUI.append("MPが不足しており、スキルを使用できません！ 再度選択してください。");
                            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                            return; // Do not proceed with skill usage
                        }

                        // MP 소모
                        player.mp -= selectedSkill.mpCost;
                        if (player.mp < 0) player.mp = 0; // Ensure MP doesn't go negative

                        // 스킬 사용
                        if (skillIndex == 0) {
                            player.ultimate(enemy);
                        } else if (skillIndex == 1) {
                            player.mainSkill(enemy);
                        } else {
                            player.normalSkill(enemy);
                        }

                        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                        // UI 업데이트
                        gameUI.playerHpBar.setValue(player.hp);
                        gameUI.enemyHpBar.setValue(enemy.hp);
                        gameUI.playerMpBar.setValue(player.mp); // MP 업데이트
                        gameUI.enemyMpBar.setValue(enemy.mp); // 적 MP도 업데이트 (만약 적도 소모한다면)

                        skillChoice.set(true);
                        latch.countDown();
                    } else {
                        gameUI.append("1~3の数字を入力してください！");
                        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                    }
                } catch (NumberFormatException ex) {
                    gameUI.append("数字を入力してください！");
                    gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                }
            });

            // Wait for selection
            try {
                gameUI.append("\nあなたのターンです！ スキルを選んでください:");
                gameUI.append("1. " + player.serverSkills[0].name + "  2. " + player.serverSkills[1].name + "  3. " + player.serverSkills[2].name);
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            handleDefeat();
        }
    }

    public void processEnemyTurn() {
        if (enemy.alive()) {
            gameUI.append("\n敵の攻撃ターンです！");
            int enemyAttack = random.nextInt(3);
            // 적 행동 로직 (단순화: 랜덤 스킬 사용)
            // 실제로는 적도 MP 소모 등을 해야 하지만, 현재 Enemy AI가 단순하므로 생략하거나 추후 추가.
            // 여기서는 UI 업데이트만 확실히.
            switch (enemyAttack) {
                case 0 -> enemy.ultimate(player);
                case 1 -> enemy.mainSkill(player);
                default -> enemy.normalSkill(player);
            }

            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            gameUI.playerHpBar.setValue(player.hp);
            gameUI.enemyHpBar.setValue(enemy.hp);
            gameUI.playerMpBar.setValue(player.mp);
            gameUI.enemyMpBar.setValue(enemy.mp);
        } else {
            handleVictory();
        }
    }

    public void checkVictory() {
        if (!player.alive()) {
            gameUI.append("\nあなたの敗北です...");
        } else if (!enemy.alive()) {
            gameUI.append("\nあなたの勝利です！");
        } else {
            if (player.hp > enemy.hp) {
                gameUI.append("\n体力が多いため、あなたの勝利です！");
            } else if (player.hp < enemy.hp) {
                gameUI.append("\n体力が少ないため、あなたの敗北です...");
            } else {
                 gameUI.append("\n引き分けです！");
            }
        }
    }

    private void handleVictory() {
        gameUI.append("\nあなたの勝利です！");
        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
        Timer timer = new Timer(5000, e -> System.exit(0));
        timer.setRepeats(false);
        timer.start();
    }

    private void handleDefeat() {
        gameUI.append("\nあなたの敗北です...");
        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
        Timer timer = new Timer(5000, e -> System.exit(0));
        timer.setRepeats(false);
        timer.start();
    }
}
