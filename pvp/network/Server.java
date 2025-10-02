package pvp.network;

import pvp.domain.character.*;
import pvp.domain.skill.JobSkill;
import pvp.domain.character.Mage;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Integer.parseInt;


public class Server {
    public static JobSkill bleedingSkill = new JobSkill("지속데미지", false, -10, 100, -1, 0);
    public static boolean isCritical = false;
    public static boolean isHit = false;
    // 게임 상태 정보
    public AtomicBoolean turn = new AtomicBoolean(false);
    // 네트워크
    ArrayList<Session1> sessions = new ArrayList<>();
    AtomicBoolean gameOver = new AtomicBoolean(false);
    // 캐릭터
    private Job player1;
    private Job player2;
    // 스킬 상태 관리
    private JobSkill skill;

    public void server() {
        try {
            // 클라이언트 연결 받기
            {
                ServerSocket serverSocket = new ServerSocket(8888);
                System.out.println("Waiting for connection...");
                ConnectTask task = new ConnectTask(serverSocket, sessions);
                new Thread(task).start();
                new Thread(task).start();
            }

            // 연결성공(매칭) 대기 & 성공 여부 전송
            //todo waitingMatching 개선하기(연결과 매칭이 서로 독립적으로 동작하도록 개선하기)
            {
                waitingMatching();
                if (sessions.get(0).in.readLine().trim().equals("requestMatching")) {
                    sessions.get(0).out.println("isMatched=true");
                }
                if (sessions.get(1).in.readLine().trim().equals("requestMatching")) {
                    sessions.get(1).out.println("isMatched=true");
                }
            }

            // 선공 정보 전송
            {
                // 공격 순서 정하기
                boolean whoFirst = new Random().nextBoolean();
                turn.set(whoFirst);
                sessions.get(0).out.println("whoFirst=" + whoFirst);
                sessions.get(1).out.println("whoFirst=" + !whoFirst);
            }

            // 캐릭터 생성
            {
                // 캐릭터 선택 받기
                int client1Choice = parseInt(sessions.get(0).in.readLine());
                int client2Choice = parseInt(sessions.get(1).in.readLine());
                // 캐릭터 생성
                player1 = createChar(client1Choice);
                player2 = createChar(client2Choice);
                // 적 캐릭터 정보 전송
                if (sessions.get(0).in.readLine().trim().equals("requestStatus")) {
                    sendStatus(sessions.get(0).out, client2Choice, player1.getHp(), player2.getHp(), player1.getMp(), player2.getMp());
                }
                if (sessions.get(1).in.readLine().trim().equals("requestStatus")) {
                    sendStatus(sessions.get(1).out, client1Choice, player2.getHp(), player1.getHp(), player2.getMp(), player1.getMp());
                }
            }


            // 전투 진행1: 클라1 공격 시 클라2 HP 반영
            new Thread(() -> {
                battleLogic(sessions.get(0).in, 1);
            }).start();

            // 전투 진행2: 클라2 공격 시 클라1 HP 반영
            new Thread(() -> {
                battleLogic(sessions.get(1).in, 2);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo 임계영역 공부하고 임계영역으로 바꾸기
    private void waitingMatching() {
        while (true) {
            if (sessions.size() == 2) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private Job createChar(int choice) {
        return switch (choice) {
            case 1 -> new Mage();
            case 2 -> new Gunner();
            case 3 -> new Priest();
            default -> new SwordMaster();
        };
    }

    private void sendStatus(PrintWriter out, int enemyChoice, int myHp, int enemyHp, int myMp, int enemyMp) {
        HashMap<String, Integer> status = new HashMap<>();
        status.put("enemyChoice", enemyChoice);
        status.put("myHp", myHp);
        status.put("enemyHp", enemyHp);
        status.put("myMp", myMp);
        status.put("enemyMp", enemyMp);
        out.println("enemyChoice=" + enemyChoice + ",myHp=" + myHp + ",enemyHp=" + enemyHp + ",myMp=" + myMp + ",enemyMp=" + enemyMp);
    }

    private void battleLogic(BufferedReader in, int client) {
        try {
            while (!gameOver.get()) {
                // 자신의 턴이 아니면 대기
                if (client == 1 && !turn.get() || client == 2 && turn.get()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    // 스킬 선택 받기
                    String s = in.readLine();
                    if (s == null) {
                        break;
                    }
                    int skillChoice = parseInt(s);

                    // 데미지를 상대 HP에 반영
                    applyDamage(client, skillChoice);

                    // 생존 확인
                    int check;
                    if (client == 1) {
                        if (player2.getHp() <= 0) {
                            check = 0;
                        } else {
                            check = 1;
                        }
                    } else {
                        if (player1.getHp() <= 0) {
                            check = 0;
                        } else {
                            check = 1;
                        }
                    }

                    // 공격 결과 전송
                    if (in.readLine().trim().equals("requestAttackResult")) {
                        sendResult(sessions.get(0).out, sessions.get(1).out, client, skillChoice, check);
                    }

                    // HP가 0이면 gameover
                    if (player1.getHp() <= 0 || player2.getHp() <= 0) {
                        gameOver.set(true);
                        break;
                    }

                    // 턴 넘기기
                    if (client == 1) {
                        turn.set(false);
                    } else {
                        turn.set(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            gameOver.set(true);
        }
    }

    private void applyDamage(int client, int skillChoice) {
        if (client == 1) {
            skill = player1.getServerSkill(skillChoice - 1);
            if (player2.getBleeding() > 0) {
                player2 = player2.applySkill(bleedingSkill); // 지속 데미지 스킬 로직
            }
            if (skill.isSelfHeal()) {
                player1 = player1.applySkill(skill);// HP 회복 스킬 로직
            } else {
                player2 = player2.applySkill(skill); // 일반 스킬 로직
            }
        } else {
            skill = player2.getServerSkill(skillChoice - 1);
            if (player1.getBleeding() > 0) {
                player1 = player1.applySkill(bleedingSkill); // 지속 데미지 스킬 로직
            }
            if (skill.isSelfHeal()) {
                player2 = player2.applySkill(skill);// HP 회복 스킬 로직
            } else {
                player1 = player1.applySkill(skill); // 일반 스킬 로직
            }
        }
    }

    private void sendResult(PrintWriter out1, PrintWriter out2, int client, int skillChoice, int check) {
        int hit;
        if (isHit) {
            hit = 1;
        } else {
            hit = 0;
        }
        int critical;
        if (isCritical) {
            critical = 1;
        } else {
            critical = 0;
        }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("client", client);
        result.put("skillChoice", skillChoice);
        result.put("isHit", hit);
        result.put("isCritical", critical);
        result.put("damage", skill.effectValue());
        result.put("player1Hp", player1.getHp());
        result.put("player2Hp", player2.getHp());
        result.put("player1Mp", player1.getMp());
        result.put("player2Mp", player2.getMp());
        result.put("check", check);

        out1.println(
                "client=" + client +
                        ",skillChoice=" + skillChoice +
                        ",isHit=" + hit +
                        ",isCritical=" + critical +
                        ",damage=" + skill.effectValue() +
                        ",player1Hp=" + player1.getHp() +
                        ",player2Hp=" + player2.getHp() +
                        ",player1Mp=" + player1.getMp() +
                        ",player2Mp=" + player2.getMp() +
                        ",check=" + check
        );

        out2.println(
                "client=" + client +
                        ",skillChoice=" + skillChoice +
                        ",isHit=" + hit +
                        ",isCritical=" + critical +
                        ",damage=" + skill.effectValue() +
                        ",player1Hp=" + player1.getHp() +
                        ",player2Hp=" + player2.getHp() +
                        ",player1Mp=" + player1.getMp() +
                        ",player2Mp=" + player2.getMp() +
                        ",check=" + check
        );

        // 초기화
        isCritical = false;
        isHit = false;
    }
}

// 서버에 스레드가 있고 요청을 받으면 해당 정보를 보내줌
// 요청에 대한 규칙을 정하고 정해진 규칙에 따라 정보를 줌
