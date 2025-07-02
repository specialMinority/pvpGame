package pvp;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLogic {

    private final GameUI gameUI;
    private volatile int choice = -1;
    private boolean characterChoice = false;
    private Random random = new Random();
    private AtomicBoolean skillChoice1 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice2 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice3 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice4 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice5 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice6 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice7 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice8 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice9 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice10 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice11 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice12 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice13 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice14 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice15 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice16 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice17 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice18 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice19 = new AtomicBoolean(false);
    private AtomicBoolean skillChoice20 = new AtomicBoolean(false);
    private Character player;
    private Character enemy;
    public GameLogic(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void gameLogic() {
        gameUI.append("마법사: 데미지가 매우 강하지만 명중률이 낮고 체력이 낮습니다.");
        gameUI.append("거너: 데미지는 강한 편이지만 명중률이 다소 낮고 체력은 낮은 편입니다.");
        gameUI.append("소드마스터: 데미지, 명중률, 체력의 밸런스가 좋습니다.");
        gameUI.append("프리스트: 데미지가 약하지만 명중률이 높고 체력이 높습니다.");

        // 사용자 캐릭터 선택
        gameUI.append("");
        gameUI.append("캐릭터를 고르세요!");
        gameUI.append("1. 마법사  2. 거너  3. 프리스트  4. 소드마스터");

        waitForCharacterChoice();
        startBattle();
    }

    private void waitForCharacterChoice() {

        gameUI.getSubmitButton().addActionListener(e -> {
            String input = gameUI.getInputText().getText().trim();
            int val = Integer.parseInt(input);
            if (val >= 1 && val <= 4) {
                choice = val;
                gameUI.append("\n선택된 캐릭터 번호: " + choice);
                gameUI.getInputText().setText("");

                if (choice == 1) {
                    player = new Mage("player", gameUI);
                } else if (choice == 2) {
                    player = new gunner("player", gameUI);
                } else if (choice == 3) {
                    player = new Priest("player", gameUI);
                } else {
                    player = new SwordMaster("player", gameUI);
                }
                characterChoice = true;
                gameUI.append("캐릭터를 선택하셨습니다.");
            } else {
                gameUI.append("1~4 중에서 입력해주세요.");
            }
        });

        //사용자가 캐릭터를 고를 때까지 대기
        while (!characterChoice) {
            System.out.println("5초마다 출력됩니다!");
            try {
                Thread.sleep(5000); // 5000밀리초 = 5초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // 적 캐릭터 무작위 생성
        Random rand = new Random();

        int enemyCharacter = rand.nextInt(4);
        if (enemyCharacter == 0) {
            enemy = new Mage("enemy", gameUI);
        } else if (enemyCharacter == 1) {
            enemy = new gunner("enemy", gameUI);
        } else if (enemyCharacter == 2) {
            enemy = new Priest("enemy", gameUI);
        } else {
            enemy = new SwordMaster("enemy", gameUI);
        }

        String enemyCha;
        if (enemy.hp == 100) {
            enemyCha = "마법사";
        } else if (enemy.hp == 120) {
            enemyCha = "거너";
        } else if (enemy.hp == 150) {
            enemyCha = "프리스트";
        } else {
            enemyCha = "소드마스터";
        }
        gameUI.append("적의 캐릭터는" + enemyCha + "입니다!");
    }

    private void userTurn(AtomicBoolean skillChoice) {
        if (player.alive()) {
            gameUI.append("\n플레이어가 공격할 차례입니다!");
            gameUI.append("공격 방식을 정해주세요");
            for (int i = 0; i < player.skills.length; i++) {
                gameUI.append((i + 1) + ". " + player.skills[i].name +
                        " 데미지:" + player.skills[i].damage +
                        " 명중률:" + player.skills[i].accuracy + "%");
            }
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());

            gameUI.getSubmitButton().addActionListener(e -> {
                String input = gameUI.getInputText().getText().trim();
                try {
                    int val = Integer.parseInt(input);
                    if (val >= 1 && val <= 3) {
                        choice = val;
                        gameUI.append("\n선택된 스킬 번호: " + choice);
                        gameUI.getInputText().setText("");
                        if (choice == 1) {
                            player.ultimate(enemy);
                            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                        } else if (choice == 2) {
                            player.mainSkill(enemy);
                            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                        } else {
                            player.normalSkill(enemy);
                            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                        }
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
            Timer timer = new Timer(5000, e -> System.exit(0));
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
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            } else if (enemyAttack == 1) {
                enemy.mainSkill(player);
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            } else {
                enemy.normalSkill(player);
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            }
        } else {
            gameUI.append("\n플레이어 승리!");
            gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            Timer timer = new Timer(5000, e -> System.exit(0));
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

    private void startBattle() {
        // 턴제 게임을 위한 코드
        // 먼저 턴이 돌게 해야 됨 이걸 어떻게 구현할거냐
        // 그리고 플레이어의 턴일 때 플레이어가 스킬을 고를 때까지 다음 턴으로 넘어가지 말아야 하고,
        // 2번째 턴부터는 적 혹은 플레이어가 죽었는지 살았는지에 대해 확인하는 코드 & 종료 코드 필요함

        boolean playerTurn = random.nextBoolean();

        gameUI.append(playerTurn ? "\n플레이어가 선공합니다!" : "\n적이 선공합니다!");

        if (playerTurn) {
            //1번째 턴
            userTurn(skillChoice1);
            enemyTurn();
            userTurn(skillChoice2);
            enemyTurn();
            userTurn(skillChoice3);
            enemyTurn();
            userTurn(skillChoice4);
            enemyTurn();
            userTurn(skillChoice5);
            enemyTurn();
            userTurn(skillChoice6);
            enemyTurn();
            userTurn(skillChoice7);
            enemyTurn();
            userTurn(skillChoice8);
            enemyTurn();
            userTurn(skillChoice9);
            enemyTurn();
            userTurn(skillChoice10);
            enemyTurn();
            userTurn(skillChoice11);
            enemyTurn();
            userTurn(skillChoice12);
            enemyTurn();
            userTurn(skillChoice13);
            enemyTurn();
            userTurn(skillChoice14);
            enemyTurn();
            userTurn(skillChoice15);
            enemyTurn();
            userTurn(skillChoice16);
            enemyTurn();
            userTurn(skillChoice17);
            enemyTurn();
            userTurn(skillChoice18);
            enemyTurn();
            userTurn(skillChoice19);
            enemyTurn();
            userTurn(skillChoice20);
            enemyTurn();
            checkVictory();
        } else {
            enemyTurn();
            userTurn(skillChoice1);
            enemyTurn();
            userTurn(skillChoice2);
            enemyTurn();
            userTurn(skillChoice3);
            enemyTurn();
            userTurn(skillChoice4);
            enemyTurn();
            userTurn(skillChoice5);
            enemyTurn();
            userTurn(skillChoice6);
            enemyTurn();
            userTurn(skillChoice7);
            enemyTurn();
            userTurn(skillChoice8);
            enemyTurn();
            userTurn(skillChoice9);
            enemyTurn();
            userTurn(skillChoice10);
            enemyTurn();
            userTurn(skillChoice11);
            enemyTurn();
            userTurn(skillChoice12);
            enemyTurn();
            userTurn(skillChoice13);
            enemyTurn();
            userTurn(skillChoice14);
            enemyTurn();
            userTurn(skillChoice15);
            enemyTurn();
            userTurn(skillChoice16);
            enemyTurn();
            userTurn(skillChoice17);
            enemyTurn();
            userTurn(skillChoice18);
            enemyTurn();
            userTurn(skillChoice19);
            enemyTurn();
            userTurn(skillChoice20);
            checkVictory();
        }

    }
}

//        while (player.alive() && enemy.alive()) {
//            if (playerTurn) {
//                gameUI.append("");
//                gameUI.append("플레이어가 공격할 차례입니다!");
//                gameUI.append("공격 방식을 정해주세요");
//                for (int i = 0; i < player.skills.length; i++) {
//                    gameUI.append((i + 1) + ". " + player.skills[i].name + " 데미지:" + player.skills[i].damage + " 명중률:" + player.skills[i].accuracy + "%");
//                }
//                if (userSkillChoice == 1) {
//                    player.ultimate(enemy);
//                } else if (choice == 2) {
//                    player.mainSkill(enemy);
//                } else {
//                    player.normalSkill(enemy);
//                }
//            } else {
//                gameUI.append("적이 공격할 차례입니다!");
//                int enemyAttack = random.nextInt(3);
//                if (enemyAttack == 0) {
//                    enemy.ultimate(player);
//                } else if (enemyAttack == 1) {
//                    enemy.mainSkill(player);
//                } else {
//                    enemy.normalSkill(player);
//                }
//            }
//            playerTurn = !playerTurn;
//        }
//        if (!player.alive()) {
//            gameUI.append("");
//            gameUI.append("플레이어 패배!");
//        } else if (!enemy.alive()) {
//            gameUI.append("");
//            gameUI.append("플레이어 승리!");
//        }
//    }