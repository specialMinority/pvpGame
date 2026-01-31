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


/**
 * The main server class for handling multiplayer connections.
 * Manages game sessions, client communication, and game state.
 * <p>
 * Refactored to remove global static variables in favor of instance-based state management
 * using {@link pvp.domain.battle.BattleResult}.
 */
public class Server {
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
    private final JobSkill bleedingSkill = new JobSkill("지속데미지", false, -10, 100, -1, 0);

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

                    // 데미지를 상대 HP에 반영 및 결과 수신
                    pvp.domain.battle.BattleResult result = applyDamage(client, skillChoice);

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
                        sendResult(sessions.get(0).out, sessions.get(1).out, client, skillChoice, check, result);
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

    private pvp.domain.battle.BattleResult applyDamage(int client, int skillChoice) {
        pvp.domain.battle.BattleResult result;
        if (client == 1) {
            skill = player1.getServerSkill(skillChoice - 1);

            if (player2.getBleeding() > 0) {
                // Bleeding effect is separate, handled simply here as applySkill returns result.
                // Assuming bleedingSkill logic is consistent
                pvp.domain.battle.BattleResult bleedRes = player2.applySkill(bleedingSkill);
                if(bleedRes.nextState() != null) player2 = bleedRes.nextState();
            }

            if (skill.isSelfHeal()) {
                result = player1.applySkill(skill);
                if (result.nextState() != null) player1 = result.nextState();
            } else {
                // For damaging skills, apply to enemy
                 // But applySkill consumes MP of 'this', so we need calls on both?
                 // No, applySkill logic in Job handles MP consumption AND effects.
                 // This is tricky because calculate damage on enemy consumes enemy MP? No.
                 // Original logic:
                 // Job next = player2.applySkill(skill); -> Enemy takes damage
                 // But player1 needs to consume MP.
                 // This logic was flawed or Job.applySkill does too much.
                 
                 // Job.applySkill context: "this" is the target taking damage OR the one casting heal?
                 // Let's check Job.applySkill again.
                 // It reduces MP of 'this'.
                 // So if player1 attacks player2:
                 // player1 consumes MP. player2 takes damage.
                 
                 // Current Job.applySkill does: reduce MP, check hit, check critical, reduce HP.
                 // It seems 'applySkill' is designed as "Character USES skill ON SELF or RECEIVES effect".
                 // BUT wait, original code:
                 // Job next = player2.applySkill(skill);
                 // player2 is taking damage? But applySkill reduces MP!
                 // This implies Player2 loses MP when Player1 attacks??
                 
                 // Let's assume for now we just want to remove statics.
                 // We will replicate original logic structure but use BattleResult.
                 
                 int attackerMpBefore = player1.getMp();
                 // Logic from original:
                 // Job next = player2.applySkill(skill); -> This reduces player2's MP? That seems like a bug in original code or I misunderstand.
                 // Original: 
                 // Job next = player2.applySkill(skill);
                 // if (next != null) {
                 //    player2 = next.withMp(defenderMpBefore); // Restore defender MP!
                 //    player1 = player1.withMp(player1.getMp() - skill.mpCost()); // Deduct attacker MP
                 // }
                 
                 int defenderMpBefore = player2.getMp();
                 result = player2.applySkill(skill);
                 
                 if (result.nextState() != null) {
                     // Restore defender MP because they shouldn't lose text
                     Job fixedEnemy = result.nextState().withMp(defenderMpBefore);
                     player2 = fixedEnemy;
                     
                     // Deduct attacker MP
                     player1 = player1.withMp(player1.getMp() - skill.mpCost());
                 }
            }
        } else {
             skill = player2.getServerSkill(skillChoice - 1);

            if (player1.getBleeding() > 0) {
                pvp.domain.battle.BattleResult bleedRes = player1.applySkill(bleedingSkill);
                 if(bleedRes.nextState() != null) player1 = bleedRes.nextState();
            }

            if (skill.isSelfHeal()) {
                result = player2.applySkill(skill);
                 if (result.nextState() != null) player2 = result.nextState();
            } else {
                 int defenderMpBefore = player1.getMp();
                 
                 result = player1.applySkill(skill);
                 
                 if (result.nextState() != null) {
                     Job fixedEnemy = result.nextState().withMp(defenderMpBefore);
                     player1 = fixedEnemy;
                     
                     player2 = player2.withMp(player2.getMp() - skill.mpCost());
                 }
            }
        }
        return result;
    }

    private void sendResult(PrintWriter out1, PrintWriter out2, int client, int skillChoice, int check, pvp.domain.battle.BattleResult result) {
        int hit = result.isHit() ? 1 : 0;
        int critical = result.isCritical() ? 1 : 0;
        int damage = result.damage();

        HashMap<String, Integer> resMap = new HashMap<>(); // unused but kept for structure if needed
        
        String msg = "client=" + client +
                        ",skillChoice=" + skillChoice +
                        ",isHit=" + hit +
                        ",isCritical=" + critical +
                        ",damage=" + damage +
                        ",player1Hp=" + player1.getHp() +
                        ",player2Hp=" + player2.getHp() +
                        ",player1Mp=" + player1.getMp() +
                        ",player2Mp=" + player2.getMp() +
                        ",check=" + check;

        out1.println(msg);
        out2.println(msg);
    }
}

// 서버에 스레드가 있고 요청을 받으면 해당 정보를 보내줌
// 요청에 대한 규칙을 정하고 정해진 규칙에 따라 정보를 줌
