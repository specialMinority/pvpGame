package pvp.network;
//todo 안되는 거 되게 하기, 반복 하기, ???

import pvp.GameUI;
import pvp.domain.character.*;
import pvp.domain.character.Mage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class Client {
    private final GameUI gameUI;

    // 네트워크
    private BufferedReader in;
    private PrintWriter out;

    // 게임 상태 정보
    private volatile boolean characterChoice = true;
    private volatile boolean firstTurn = false;
    private volatile boolean gameOver = false;
    private volatile int selectedSkill = -1;

    // cleint1/2 구분
    private Boolean whatClient = null;

    // 내 정보 & 상대 정보
    private Job player;
    private Job enemy;

    public Client(GameUI gameUI) {
        this.gameUI = gameUI;
        gameUI.submitButton.addActionListener(e -> {
            String text = gameUI.getInputText().getText().trim();
            if (!text.isEmpty()) {
                try {
                    int val = Integer.parseInt(text);
                    gameUI.getInputText().setText("");
                    int myChar;
                    if (characterChoice) {
                        if (val >= 1 && val <= 4) {
                            myChar = val;
                            // 내 캐릭터 생성
                            newCharacter(myChar);
                            out.println(myChar);
                            gameUI.append("[시스템]캐릭터 선택: " + player.getName());
                            characterChoice = false;
                        } else {
                            gameUI.append("[시스템]1~4 중에서 입력해주세요.");
                        }
                    } else {
                        if (firstTurn) {
                            if (val >= 1 && val <= 4) {
                                selectedSkill = val;
                                gameUI.append("[시스템]스킬 선택: " + player.getServerSkill(selectedSkill - 1).name());
                            } else {
                                gameUI.append("[시스템]스킬은 1~4 중에서 선택하세요.");
                            }
                        } else {
                            gameUI.append("[시스템]지금은 상대 턴입니다. 잠깐 기다리세요.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    gameUI.append("[시스템]숫자를 입력하삼");
                }
            } else {
                gameUI.append("[시스템]값을 입력하삼");
            }
        });
    }

    public void start() {
        try {
            // 서버 연결
            {
                Socket server = new Socket("localhost", 8888);
                in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                out = new PrintWriter(server.getOutputStream(), true);
                gameUI.append("[시스템]서버에 접속됐습니다");
            }

            // 매칭 확인
            {
                // 매칭 요청
                out.println("requestMatching");
                gameUI.append("[시스템]상대와 매칭될 때까지 대기합니다");
                String[] strings = in.readLine().split("=");
                if (strings[0].trim().equals("isMatched")) {
                    if (strings[1].trim().equals("true")) {
                        gameUI.append("[시스템]상대와 매칭되었습니다!");
                    }
                }
            }

            // 선공 결정
            {
                String[] strings = in.readLine().split("=");
                if (strings[0].trim().equals("whoFirst")) {
                    if (strings[1].trim().equals("true")) {
                        gameUI.append("[시스템]당신이 선제 공격입니다!");
                        firstTurn = true;
                    } else {
                        gameUI.append("[시스템]적이 먼저 공격합니다!");
                    }
                }
            }

            // 초기 안내
            {
                gameUI.append("[공지]마법사: 데미지가 매우 강하지만 명중률이 낮고 체력이 낮습니다. 특수 스킬: 원킬기");
                gameUI.append("[공지]거너: 데미지는 강한 편이지만 명중률이 다소 낮고 체력은 낮은 편입니다. 특수 스킬: 극데미지");
                gameUI.append("[공지]소드마스터: 데미지, 명중률, 체력의 밸런스가 좋습니다. 특수 스킬: 지속 데미지");
                gameUI.append("[공지]프리스트: 데미지가 약하지만 명중률이 높고 체력이 높습니다. 특수 스킬: 체력 회복");
                gameUI.append("\n[공지]내 HP는 파란색, 적의 HP는 빨간색입니다!");
            }

            new Thread(() -> {
                try {
                    // 내 캐릭터 생성
                    {
                        gameUI.append("\n[시스템]캐릭터를 고르세요!");
                        gameUI.append("[사용자 입력]1. 마법사  2. 거너  3. 프리스트  4. 소드마스터");
                        waitingCharChoice();
                        System.out.println("탈출");
                    }

                    // 상태 정보 받기
                    {
                        out.println("requestStatus");
                        HashMap<String, Integer> status = new HashMap<>();
                        String[] strings = in.readLine().split(",");
                        for (String string : strings) {
                            String[] words = string.split("=");
                            String key = words[0].trim();
                            int value = Integer.parseInt(words[1].trim());
                            status.put(key, value);
                        }
                        int enemyChoice = status.get("enemyChoice");
                        int myHp = status.get("myHp");
                        int enemyHp = status.get("enemyHp");
                        int myMp = status.get("myMp");
                        int enemyMp = status.get("enemyMp");

                        //todo JAVA 불변객체에 대해 공부, player를 불변객체로 만들기

                        // 적 캐릭터 생성
                        newEnemyChar(enemyChoice);
                        gameUI.append("[시스템]상대 캐릭터: " + enemy.getName());

                        // HP, MP 세팅
                        player = player.withHp(myHp);
                        enemy  = enemy.withHp(enemyHp);
                        player = player.withMp(myMp);
                        enemy  = enemy.withMp(enemyMp);
                        gameUI.playerHpBar.setMaximum(player.getHp());
                        gameUI.playerHpBar.setValue(player.getHp());
                        gameUI.playerMpBar.setMaximum(player.getMp());
                        gameUI.playerMpBar.setValue(player.getMp());
                        gameUI.enemyHpBar.setMaximum(enemy.getHp());
                        gameUI.enemyHpBar.setValue(enemy.getHp());
                        gameUI.enemyMpBar.setMaximum(enemy.getMp());
                        gameUI.enemyMpBar.setValue(enemy.getMp());
                    }

                    // 선공 여부
                    if (firstTurn) {
                        ChoiceYourSkill();
                    } else {
                        gameUI.append("[시스템]상대 턴입니다. 잠깐 기다리세요.");
                    }

                    // 전투 진행
                    while (!gameOver) {
                        if (firstTurn) {
                            // 스킬선택 & 서버전송
                            {
                                int skillCountdown = 0;
                                // 10초간 스킬선택 대기
                                while (selectedSkill == -1 && !gameOver && skillCountdown < 10) {
                                    try {
                                        Thread.sleep(1000);
                                        skillCountdown++;
                                        waitingSkillChoice(skillCountdown);
                                    } catch (InterruptedException ignored) {
                                    }
                                }
                                // 시간초과 시 자동선택
                                if (selectedSkill == -1) {
                                    Random random = new Random();
                                    selectedSkill = random.nextInt(4) + 1;
                                }
                                // 서버로 선택스킬 전송
                                out.println(selectedSkill);
                                // 선택 초기화
                                selectedSkill = -1;
                            }

                            // 서버에서 공격 결과 수신
                            out.println("requestAttackResult");
                            recvData();

                            // 상대 턴 넘김
                            if (!gameOver) {
                                firstTurn = false;
                                gameUI.append("[시스템]상대 턴입니다. 대기 중...");
                                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
                            }
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            recvData();

                            // 내 턴 넘김
                            if (!gameOver) {
                                firstTurn = true;
                                ChoiceYourSkill();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitingCharChoice() {
        while (characterChoice) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void newCharacter(int choice) {
        player = switch (choice) {
            case 1 -> new Mage();
            case 2 -> new Gunner();
            case 3 -> new Priest();
            default -> new SwordMaster();
        };
    }

    private void newEnemyChar(int choice) {
        enemy = switch (choice) {
            case 1 -> new Mage();
            case 2 -> new Gunner();
            case 3 -> new Priest();
            default -> new SwordMaster();
        };
    }

    private void ChoiceYourSkill() {
        gameUI.append("\n[사용자 입력]공격 방식을 정해주세요");
        gameUI.append("1. " + player.getServerSkill(0).name() + " 데미지: " + Math.abs(player.getServerSkill(0).effectValue()) + " 명중률: " + player.getServerSkill(0).accuracy());
        gameUI.append("2. " + player.getServerSkill(1).name() + " 데미지: " + Math.abs(player.getServerSkill(1).effectValue()) + " 명중률: " + player.getServerSkill(1).accuracy());
        gameUI.append("3. " + player.getServerSkill(2).name() + " 데미지: " + Math.abs(player.getServerSkill(2).effectValue()) + " 명중률: " + player.getServerSkill(2).accuracy());
        gameUI.append("4. " + player.getServerSkill(3).name() + " 데미지: " + Math.abs(player.getServerSkill(3).effectValue()) + " 명중률: " + player.getServerSkill(3).accuracy());
        gameUI.append("\n[경고]10초 안에 스킬을 고르지 않으면 자동으로 스킬이 선택되고 상대턴으로 넘어갑니다\n");
    }

    private void waitingSkillChoice(int waited) {
        gameUI.append("[시스템]스킬 선택까지" + (11 - waited) + "초 남았습니다");
        gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
    }

    private void recvData() throws IOException {
        HashMap<String, Integer> results = new HashMap<>();
        String[] strings = in.readLine().split(",");
        for (String string : strings) {
            String[] words = string.split("=");
            String key = words[0].trim();
            int value = Integer.parseInt(words[1].trim());
            results.put(key, value);
        }
        boolean isCritical;
        int clientId = results.get("client");
        int skillChoice = results.get("skillChoice");
        int hit = results.get("isHit");
        if(results.get("isCritical") == 1) {
            isCritical = true;
        } else {
            isCritical = false;
        }
        int dmg = results.get("damage");
        int hp1 = results.get("player1Hp");
        int hp2 = results.get("player2Hp");
        int mp1 = results.get("player1Mp");
        int mp2 = results.get("player2Mp");
        int check = results.get("check");

        // client 식별
        if (whatClient == null) {
            whatClient = firstTurn == (clientId == 1);
        }

        // 공격자 식별
        boolean amIAttacker = false;
        if (whatClient) {
            if (clientId == 1) {
                amIAttacker = true;
            }
        } else {
            if (clientId == 2) {
                amIAttacker = true;
            }
        }

        // HP 반영, MP 반영
        if (whatClient) {
            player = player.withHp(hp1);
            enemy  = enemy.withHp(hp2);
            player = player.withMp(mp1);
            enemy  = enemy.withMp(mp2);
        } else {
            player = player.withHp(hp2);
            enemy  = enemy.withHp(hp1);
            player = player.withMp(mp2);
            enemy  = enemy.withMp(mp1);
        }

        // 전투 상황 메세지 출력
        {
            String msg;
            if (amIAttacker) {
                msg = "[전투]나(" + player.getName() + ")의 " + player.getServerSkill(skillChoice - 1).name();
                if (hit == 1) {
                    if (player.getName().equals("프리스트") && skillChoice == 4) {
                        msg = msg + " 버프 스킬 발동! 내 HP 10회복";
                    } else if (player.getName().equals("소드마스터") && skillChoice == 4) {
                        msg = msg + " 출혈 스킬 발동! 2턴 동안 상대 HP 10씩 감소";
                    } else if (isCritical) {
                        msg = msg + " 명중! 크리티컬! 데미지: " + (int) (dmg * 1.3);
                    } else {
                        msg = msg + " 명중! 데미지 " + dmg;
                    }
                } else {
                    msg = msg + " 빗나감";
                }
            } else {
                msg = "[전투]상대(" + enemy.getName() + ")의 " + enemy.getServerSkill(skillChoice - 1).name();
                if (hit == 1) {
                    if (enemy.getName().equals("프리스트") && skillChoice == 4) {
                        msg = msg + " 버프 스킬 발동! 상대 HP 10회복";
                    } else if (enemy.getName().equals("소드마스터") && skillChoice == 4) {
                        msg = msg + " 출혈 스킬 발동! 2턴 동안 HP가 10씩 감소합니다";
                    } else if (isCritical) {
                        msg = msg + " 명중! 크리티컬! 데미지: " + (int) (dmg * 1.3);
                    } else {
                        msg = msg + " 명중! 데미지 " + dmg;
                    }
                } else {
                    msg = msg + " 빗나감";
                }
            }
            gameUI.append("\n" + msg + "\n");
        }

        // hp, mp 상황 반영
        {
            gameUI.playerHpBar.setValue(player.getHp());
            gameUI.enemyHpBar.setValue(enemy.getHp());
            gameUI.playerMpBar.setValue(player.getMp());
            gameUI.enemyMpBar.setValue(enemy.getMp());
            gameUI.append("[전투]내 HP=" + player.getHp() + " / 내 MP=" + player.getMp() + " / 상대 HP=" + enemy.getHp() + " / 상대 MP=" + enemy.getMp());
        }

        //승패 판단
        if (check == 0 || player.getHp() <= 0 || enemy.getHp() <= 0) {
            gameOver = true;
            if (player.getHp() <= 0) {
                gameUI.append("[전투]패배");
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            } else {
                gameUI.append("[전투]승리");
                gameUI.logArea.setCaretPosition(gameUI.logArea.getDocument().getLength());
            }
        }
    }
}
