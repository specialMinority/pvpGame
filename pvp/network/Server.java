package pvp.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class Server {

    public volatile int client1Hp;
    public volatile int client2Hp;
    private boolean whoFirst;
    AtomicBoolean turn = new AtomicBoolean(whoFirst);
    AtomicBoolean gameOver = new AtomicBoolean(false);


    public static void main(String[] args) {
        Server server = new Server();
        server.server();
    }

    public void server() {
        try {
            // 서버 소켓 생성 & 클라이언트 연결 받기
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Waiting for connection...");
            Socket client1 = serverSocket.accept();
            System.out.println("클라이언트가 접속했습니다");
            Socket client2 = serverSocket.accept();
            System.out.println("클라이언트가 접속했습니다");
            boolean clientConnecting = true;

            // 선공 순서 부여
            Random random = new Random();
            whoFirst = random.nextBoolean();
            boolean whoSecond = !whoFirst;


            // 클라1 입출력
            BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);

            // 클라2 입출력
            BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);

            // 연결 알림 + 선공 정보
            out1.println(clientConnecting);
            out1.println(whoFirst);
            out2.println(clientConnecting);
            out2.println(whoSecond);

            // 캐릭터 선택 받기
            int client1Choice = Integer.parseInt(in1.readLine());
            int client2Choice = Integer.parseInt(in2.readLine());

            // 클라1 Hp 세팅
            if (client1Choice == 1) {
                client1Hp = 150;
            } else if (client1Choice == 2) {
                client1Hp = 170;
            } else if (client1Choice == 3) {
                client1Hp = 225;
            } else {
                client1Hp = 180;
            }

            // 클라2 Hp 세팅
            if (client2Choice == 1) {
                client2Hp = 150;
            } else if (client2Choice == 2) {
                client2Hp = 170;
            } else if (client2Choice == 3) {
                client2Hp = 225;
            } else {
                client2Hp = 180;
            }

            // 클라1한테 클라2 선택, 내 HP, 상대 HP 전송
            out1.println(client2Choice);
            out1.println(client1Hp);
            out1.println(client2Hp);

            // 클라2한테 클라1 선택, 내 HP, 상대 HP 전송
            out2.println(client1Choice);
            out2.println(client2Hp);
            out2.println(client1Hp);

            // 전투 진행1: 클라1 입력 시 클라2 HP 반영
            new Thread(() -> {
                try {
                    while (!gameOver.get()) {
                        if (!turn.get()) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ignored) {
                            }
                            continue;
                        }

                        String s = in1.readLine();
                        if (s == null) break;
                        int skillChoice = Integer.parseInt(s);

                        int acc = getAccuracy(client1Choice, skillChoice);
                        int roll = random.nextInt(100) + 1;
                        boolean hit = (roll <= acc);
                        int dmg;
                        if (hit) {
                            dmg = getDamage(client1Choice, skillChoice);
                        } else {
                            dmg = 0;
                        }

                        if (hit) {
                            client2Hp = client2Hp - dmg;
                            if (client2Hp < 0) {
                                client2Hp = 0;
                            }
                        }

                        // 공격자, 스킬, 명중여부, 데미지, HP1, HP2, check
                        out1.println(1);
                        out1.println(skillChoice);
                        if (hit) {
                            out1.println(1);
                        } else {
                            out1.println(0);
                        }
                        out1.println(dmg);
                        out1.println(client1Hp);
                        out1.println(client2Hp);
                        int check;
                        if (client2Hp <= 0) {
                            check = 0; // 게임 끝
                        } else {
                            check = 1; // 턴 넘기기
                        }
                        out1.println(check);

                        out2.println(1);
                        out2.println(skillChoice);
                        if (hit) {
                            out2.println(1);
                        } else {
                            out2.println(0);
                        }
                        out2.println(dmg);
                        out2.println(client1Hp);
                        out2.println(client2Hp);
                        out2.println(check);

                        if (client2Hp <= 0) {
                            gameOver.set(true);
                            break;
                        }

                        // 턴 교대
                        turn.set(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gameOver.set(true);
                }
            }).start();

            // 전투 진행2: 클라2 입력 시 클라1 HP 반영
            new Thread(() -> {
                try {
                    while (!gameOver.get()) {
                        if (turn.get()) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ignored) {
                            }
                            continue;
                        }

                        String s = in2.readLine();
                        if (s == null) break;
                        int skillChoice = Integer.parseInt(s);

                        int acc = getAccuracy(client2Choice, skillChoice);
                        int roll = random.nextInt(100) + 1;
                        boolean hit = (roll <= acc);
                        int dmg;
                        if (hit) {
                            dmg = getDamage(client2Choice, skillChoice);
                        } else {
                            dmg = 0;
                        }

                        if (hit) {
                            client1Hp = client1Hp - dmg;
                            if (client1Hp < 0) {
                                client1Hp = 0;
                            }
                        }

                        out1.println(2);
                        out1.println(skillChoice);
                        if (hit) {
                            out1.println(1);
                        } else {
                            out1.println(0);
                        }
                        out1.println(dmg);
                        out1.println(client1Hp);
                        out1.println(client2Hp);
                        int check2;
                        if (client1Hp <= 0) {
                            check2 = 0;
                        } else {
                            check2 = 1;
                        }
                        out1.println(check2);

                        out2.println(2);
                        out2.println(skillChoice);
                        if (hit) {
                            out2.println(1);
                        } else {
                            out2.println(0);
                        }
                        out2.println(dmg);
                        out2.println(client1Hp);
                        out2.println(client2Hp);
                        out2.println(check2);

                        if (client1Hp <= 0) {
                            gameOver.set(true);
                            break;
                        }

                        turn.set(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gameOver.set(true);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDamage(int characterChoice, int skillChoice) {
        if (characterChoice == 1) { // Mage
            if (skillChoice == 1) {
                return 90; // 메테오
            } else if (skillChoice == 2) {
                return 45; // 파이어볼
            } else {
                return 20; // 연속발사
            }
        } else if (characterChoice == 2) { // Gunner
            if (skillChoice == 1) {
                return 60; // 양자폭탄
            } else if (skillChoice == 2) {
                return 35; // 레이저바주카
            } else {
                return 15; // 게틀링건
            }
        } else if (characterChoice == 3) { // Priest
            if (skillChoice == 1) {
                return 30; // 참회의 망치
            } else if (skillChoice == 2) {
                return 24; // 디플렉트 월
            } else {
                return 12; // 순백의 칼날
            }
        } else {
            if (skillChoice == 1) {
                return 40; // 환영검무
            } else if (skillChoice == 2) {
                return 28; // 발도
            } else {
                return 14; // 리귀검술
            }
        }
    }

    private int getAccuracy(int characterChoice, int skillChoice) {
        if (characterChoice == 1) { // Mage
            if (skillChoice == 1) {
                return 40; // 메테오
            } else if (skillChoice == 2) {
                return 70; // 파이어볼
            } else {
                return 95; // 연속발사
            }
        } else if (characterChoice == 2) { // Gunner
            if (skillChoice == 1) {
                return 55; // 양자폭탄
            } else if (skillChoice == 2) {
                return 75; // 레이저바주카
            } else {
                return 95; // 게틀링건
            }
        } else if (characterChoice == 3) { // Priest
            if (skillChoice == 1) {
                return 90; // 참회의 망치
            } else if (skillChoice == 2) {
                return 95; // 디플렉트 월
            } else {
                return 100; // 순백의 칼날
            }
        } else {
            if (skillChoice == 1) {
                return 80; // 환영검무
            } else if (skillChoice == 2) {
                return 90; // 발도
            } else {
                return 100; // 리귀검술
            }
        }
    }

}
