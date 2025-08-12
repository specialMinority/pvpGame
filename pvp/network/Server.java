package pvp.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class Server {

    public int client1Hp;
    public int client2Hp;

    public static void main(String[] args) {
        Server server = new Server();
        server.server();
    }

    public void server() {
        try {
            boolean clientConnecting;
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Waiting for connection...");
            Socket client1 = serverSocket.accept();
            System.out.println("클라이언트가 접속했습니다");
            Socket client2 = serverSocket.accept();
            System.out.println("클라이언트가 접속했습니다");
            clientConnecting = true;

            if (clientConnecting) {
                Random random = new Random();
                boolean whoFirst = random.nextBoolean();
                boolean whoSecond = !whoFirst;

                new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
                        PrintWriter out = new PrintWriter(client2.getOutputStream(), true)) {
                        out.println(clientConnecting);
                        out.println(whoFirst);
                        String clientChoice = in.readLine();
                        int client1Choice = Integer.parseInt(clientChoice);
                        System.out.println(client1Choice);
                        out.println(client1Choice);
                        if (client1Choice == 1) {
                            client1Hp = 150;
                        } else if (client1Choice == 2) {
                            client1Hp = 170;
                        } else if (client1Choice == 3) {
                            client1Hp = 225;
                        } else {
                            client1Hp = 180;
                        }
                        while (true) {
                            String s = in.readLine();
                            int skillChoice = Integer.parseInt(s);
                            System.out.println(skillChoice);
                            out.println(skillChoice);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                            PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
                    ) {
                        out.println(clientConnecting);
                        out.println(whoSecond);
                        String clientChoice = in.readLine();
                        int client2Choice = Integer.parseInt(clientChoice);
                        System.out.println(client2Choice);
                        out.println(client2Choice);
                        if (client2Choice == 1) {
                            client2Hp = 150;
                        } else if (client2Choice == 2) {
                            client2Hp = 170;
                        } else if (client2Choice == 3) {
                            client2Hp = 225;
                        } else {
                            client2Hp = 180;
                        }
                        while (true) {
                            String s = in.readLine();
                            int skillChoice = Integer.parseInt(s);
                            System.out.println(skillChoice);
                            out.println(skillChoice);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                // int characterChoice
                // int skillChoice
                // )

                //보내기( 클라이언트 순서 부여: random으로 boolean값을 서버에서 생성해서 보낸다. 이후로는 client 내부로직에서
                // 상대가 뭔 캐릭터를 골랐냐: int로 넘버만 보내고 내부로직으로 처리
                // 상대가 무슨 스킬을 썼냐: 위와 동일
                // 상대가 쓴 스킬에 따른 HP 감소에 대한 로직이 서버에 있어야 되고, 서버는
                // 그 결과를 각 클라이언트한테 동시 반영, 즉 전송해야함
                // )
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            } catch(Exception e){
                e.printStackTrace();
            }
    }
}
