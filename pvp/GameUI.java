package pvp;

import javax.swing.*;
import java.awt.*;

public class GameUI {
    //창 만들기
    JFrame frame = new JFrame();
    JTextArea logArea = new JTextArea();
    JTextField inputText = new JTextField();
    JButton submitButton = new JButton("선택");
    JLabel turnLabel = new JLabel("남은 턴이 표시됩니다!", SwingConstants.CENTER);
    JProgressBar playerHpBar = new JProgressBar();
    JProgressBar enemyHpBar = new JProgressBar();

    public GameUI() {
        frame.setTitle("Game UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //게임 로그
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(centerPanel, BorderLayout.CENTER);

        //사용자 입력
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputText, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        inputPanel.setPreferredSize(new Dimension(400, 30));
        frame.add(inputPanel, BorderLayout.SOUTH);

        //턴 표시
        turnLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        turnLabel.setOpaque(true); // 배경색 보이게 하기
        turnLabel.setBackground(Color.LIGHT_GRAY); // 배경색
        turnLabel.setForeground(Color.BLACK); // 글자색

        //플레이어 체력바
        playerHpBar.setMinimum(0);        // 최소값
        playerHpBar.setStringPainted(true); // 숫자 표시 여부
        playerHpBar.setForeground(Color.BLUE); // 채워진 부분 색상

        //적 체력바
        enemyHpBar.setMinimum(0);        // 최소값
        enemyHpBar.setStringPainted(true); // 숫자 표시 여부
        enemyHpBar.setForeground(Color.RED); // 채워진 부분 색상

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(playerHpBar, BorderLayout.WEST);
        statusPanel.add(enemyHpBar, BorderLayout.EAST);
        statusPanel.add(turnLabel, BorderLayout.CENTER);
        statusPanel.setPreferredSize(new Dimension(200, 50));
        frame.add(statusPanel, BorderLayout.NORTH);

        //보이게 하기
        frame.setVisible(true);
    }

    public void append(String text) {
        logArea.append(text + "\n");
    }

    public JTextField getInputText() {
        return inputText;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }
}
