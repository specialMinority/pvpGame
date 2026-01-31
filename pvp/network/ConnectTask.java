package pvp.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ConnectTask implements Runnable {
    ServerSocket serverSocket;
    ArrayList<Session1> sessions;

    public ConnectTask(ServerSocket serverSocket, ArrayList<Session1> sessions) {
        this.serverSocket = serverSocket;
        this.sessions = sessions;
    }

    @Override
    public void run() {
        try {
            Session1 s = new Session1();
            s.socket = serverSocket.accept();
            System.out.println("클라이언트가 접속했습니다");

            s.in = new BufferedReader(new InputStreamReader(s.socket.getInputStream()));
            s.out = new PrintWriter(s.socket.getOutputStream(), true);
            sessions.add(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

