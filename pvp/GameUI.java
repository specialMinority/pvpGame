package pvp;

import javax.swing.*;
import java.awt.*;

public class GameUI {
    //창 만들기
    JFrame frame = new JFrame();

    //초기화면과 게임화면 나누기
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);
    JPanel menuPanel = new JPanel();  // 시작 화면
    JPanel gamePanel = new JPanel();  // 게임 화면
    JTextArea logArea = new JTextArea();
    JTextField inputText = new JTextField();
    JButton submitButton = new JButton("선택");
    JProgressBar playerHpBar = new JProgressBar();
    JProgressBar enemyHpBar = new JProgressBar();
    JLabel turnLabel = new JLabel("남은 턴이 표시됩니다!", SwingConstants.CENTER);
    JButton saveButton = new JButton("저장");
    JButton resetButton = new JButton("초기화");
    JButton startButton = new JButton("게임시작");
    JButton loadButton = new JButton("불러오기");

    public GameUI() {
        //창 세팅
        frame.setTitle("Game UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //초기화면
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        //여백추가
        menuPanel.setBorder(BorderFactory.createEmptyBorder(200, 200, 100, 200));

        //버튼사이즈
        Dimension buttonSize = new Dimension(200, 60); // 너비 200, 높이 60
        startButton.setMaximumSize(buttonSize);
        loadButton.setMaximumSize(buttonSize);

        //가운데정렬
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼 사이 여백
        menuPanel.add(startButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 세로 간격
        menuPanel.add(loadButton);
        mainPanel.add(menuPanel, "menu");

//        startButton.addActionListener(e -> {
//            cardLayout.show(mainPanel, "game");
//        });

        //게임패널 레이아웃 생성
        gamePanel.setLayout(new BorderLayout());

        //게임 로그
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        gamePanel.add(scrollPane, BorderLayout.CENTER);

        //사용자 입력
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputText, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        inputPanel.setPreferredSize(new Dimension(400, 30));
        gamePanel.add(inputPanel, BorderLayout.SOUTH);

        //턴 표시
        JPanel turnPanel = new JPanel();
        turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));

        turnLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        turnLabel.setOpaque(true);
        turnLabel.setForeground(Color.BLACK);

        JLabel alertLabel = new JLabel("저장은 1턴 이상 진행 후에 가능합니다");
        alertLabel.setFont(new Font("맑은 고딕", Font.BOLD, 10));
        alertLabel.setOpaque(true);
        alertLabel.setForeground(Color.RED);

        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        alertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnPanel.add(turnLabel);
        turnPanel.add(alertLabel);

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
        statusPanel.add(turnPanel, BorderLayout.CENTER);
        statusPanel.setPreferredSize(new Dimension(200, 50));
        gamePanel.add(statusPanel, BorderLayout.NORTH);

        //저장, 불러오기, 초기화 버튼
        JPanel saveButtonPanel = new JPanel();
        saveButtonPanel.setLayout(new BoxLayout(saveButtonPanel, BoxLayout.Y_AXIS)); // 수직 정렬

        // 버튼 크기 고정
        Dimension buttonsSize = new Dimension(100, 30);
        saveButton.setMaximumSize(buttonsSize);
        resetButton.setMaximumSize(buttonsSize);

        saveButton.setBackground(Color.LIGHT_GRAY); // 배경색 변경
        saveButton.setOpaque(true);                 // 배경색 보이게 설정
        resetButton.setBackground(Color.LIGHT_GRAY); // 배경색 변경
        resetButton.setOpaque(true);                 // 배경색 보이게 설정

        // 위로 몰기 위해 빈 공간 추가 없이 그냥 add
        saveButtonPanel.add(saveButton);
        saveButtonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 간격
        saveButtonPanel.add(resetButton);

        // 오른쪽 상단에 붙이기 위해 FlowLayout 사용
        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10)); // 위쪽에 붙이기
        eastPanel.add(saveButtonPanel);
        gamePanel.add(eastPanel, BorderLayout.EAST);

        //게임 진행 페널 추가
        mainPanel.add(gamePanel, "game");

        //카드 레이아웃 추가
        frame.add(mainPanel);

        //보이게 하기
        frame.setVisible(true);
    }

    public void append(String text) {
        logArea.append(text + "\n");
    }

    public JTextField getInputText() {
        return inputText;
    }

    public void showGamePanel() {
        System.out.println("카드전환");
        cardLayout.show(mainPanel, "game");
    }
}
