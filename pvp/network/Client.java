package pvp.network;

import pvp.GameUI;
import pvp.character.Gunner;
import pvp.character.Mage;
import pvp.character.Priest;
import pvp.character.SwordMaster;
import pvp.character.Character;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final GameUI gameUI;
    private AtomicBoolean connecting = new AtomicBoolean(false);
    private boolean turn;
    private int enemyCharacterChoice;
    private int enemySkillChoice;
    private List<Integer> myHp = new ArrayList<>();
    private List<Integer> enemyHp = new ArrayList<>();
    private List<Boolean> enemyAlive = new ArrayList<>();
    private Character player;
    private Character enemy;
    private boolean userCharacterChoice = false;
    private BufferedReader in;
    private PrintWriter out;


    public Client(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void start() {
        try {
            Socket server = new Socket("localhost", 8888);
            gameUI.append("서버에 접속됐음");
            gameUI.append("상대와 매칭될 때까지 대기합니다");

            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);

            new Thread(() -> {
                try {
                    //매칭 여부 확인
                    connecting.set(Boolean.parseBoolean(in.readLine()));
                    gameUI.append("상대와 매칭되었습니다!\n");

                    //선제 공격 대상 확인
                    turn = Boolean.parseBoolean(in.readLine());
                    if (turn) {
                        gameUI.append("당신이 선제 공격입니다!");
                    } else {
                        gameUI.append("적이 먼저 공격합니다!");
                    }

                    //상대 캐릭터 선택 확인 및 생성
                    enemyCharacterChoice = Integer.parseInt(in.readLine());
                    if (enemyCharacterChoice == 1) {
                        gameUI.append("\n상대는 마법사입니다!");
                    } else if (enemyCharacterChoice == 2) {
                        gameUI.append("\n상대는 거너입니다!");
                    } else if (enemyCharacterChoice == 3) {
                        gameUI.append("\n상대는 프리스트입니다!");
                    } else {
                        gameUI.append("\n상대는 소드마스터입니다!");
                    }

                    //HP 세팅
                    int myHpBarSet = Integer.parseInt(in.readLine());
                    gameUI.playerHpBar.setMaximum(myHpBarSet);
                    gameUI.playerHpBar.setValue(myHpBarSet);
                    int enemyHpBarSet = Integer.parseInt(in.readLine());
                    gameUI.enemyHpBar.setMaximum(enemyHpBarSet);
                    gameUI.enemyHpBar.setValue(enemyHpBarSet);

                    //전투진행
                    while (true) {
                        String s = in.readLine();
                        gameUI.append(s);
                        myHp.add(Integer.parseInt(in.readLine()));
                        enemyHp.add(Integer.parseInt(in.readLine()));
                        enemyAlive.add(Boolean.parseBoolean(in.readLine()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (!connecting.get()) {
                System.out.println("5초마다 출력됩니다!");
                try {
                    Thread.sleep(5000); // 5000밀리초 = 5초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            gameUI.append("마법사: 데미지가 매우 강하지만 명중률이 낮고 체력이 낮습니다.");
            gameUI.append("거너: 데미지는 강한 편이지만 명중률이 다소 낮고 체력은 낮은 편입니다.");
            gameUI.append("소드마스터: 데미지, 명중률, 체력의 밸런스가 좋습니다.");
            gameUI.append("프리스트: 데미지가 약하지만 명중률이 높고 체력이 높습니다.");

            //캐릭터 선택
            gameUI.append("");
            gameUI.append("캐릭터를 고르세요!");
            gameUI.append("1. 마법사  2. 거너  3. 프리스트  4. 소드마스터");

            gameUI.submitButton.addActionListener(e -> {
                String input = gameUI.getInputText().getText().trim();
                int intInput = Integer.parseInt(input);
                out.println(intInput);
                if (intInput >= 1 && intInput <= 4) {
                    int choice = intInput;
                    gameUI.append("\n선택된 캐릭터 번호: " + choice);
                    gameUI.getInputText().setText("");

                    if (choice == 1) {
                        player = new Mage("player", gameUI);
                    } else if (choice == 2) {
                        player = new Gunner("player", gameUI);
                    } else if (choice == 3) {
                        player = new Priest("player", gameUI);
                    } else {
                        player = new SwordMaster("player", gameUI);
                    }
                    userCharacterChoice = true;
                    gameUI.append("캐릭터를 선택하셨습니다.");
                } else {
                    gameUI.append("1~4 중에서 입력해주세요.");
                }
            });

            //사용자가 캐릭터를 고를 때까지 대기
            while (!userCharacterChoice) {
                System.out.println("5초마다 출력됩니다!");
                try {
                    Thread.sleep(5000); // 5000밀리초 = 5초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (turn && gameUI.playerHpBar.getValue() != 0) {
                gameUI.append("공격 방식을 정해주세요");
                for (int i = 0; i < player.serverSkills.length; i++) {
                    gameUI.append((i + 1) + ". " + player.serverSkills[i].name +
                            " 데미지:" + player.serverSkills[i].damage +
                            " 명중률:" + player.serverSkills[i].accuracy + "%");
                }
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());

                gameUI.submitButton.addActionListener(e -> {
                    String input = gameUI.getInputText().getText().trim();

                    int val = Integer.parseInt(input);
                    if (val >= 1 && val <= 3) {
                        int choice = val;
                        out.println(choice);
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
                    } else {
                        gameUI.append("1~3 중에서 입력해주세요.");
                        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
