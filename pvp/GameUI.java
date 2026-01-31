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
    public JTextArea logArea = new JTextArea();
    JTextField inputText = new JTextField();
    public JButton submitButton = new JButton("選択");

    // HP/MP 바
    public JProgressBar playerHpBar = new JProgressBar();
    public JProgressBar enemyHpBar = new JProgressBar();
    public JProgressBar playerMpBar = new JProgressBar(); // 추가
    public JProgressBar enemyMpBar = new JProgressBar();  // 추가

    JLabel turnLabel = new JLabel("残りのターンが表示されます！", SwingConstants.CENTER);
    JButton saveButton = new JButton("保存");
    public JButton retryButton = new JButton("再挑戦");
    public JButton exitButton = new JButton("終了");
    JButton startButton = new JButton("ゲーム開始");
    JButton loadButton = new JButton("ロード");
    JButton multiButton = new JButton("オンライン対戦");

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
        menuPanel.setBorder(BorderFactory.createEmptyBorder(150, 200, 100, 200));

        //버튼사이즈
        Dimension buttonSize = new Dimension(200, 60); // 너비 200, 높이 60
        startButton.setMaximumSize(buttonSize);
        loadButton.setMaximumSize(buttonSize);
        multiButton.setMaximumSize(buttonSize);

        //가운데정렬
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        multiButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼 사이 여백
        menuPanel.add(startButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 세로 간격
        menuPanel.add(loadButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(multiButton);
        mainPanel.add(menuPanel, "menu");

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

        turnLabel.setFont(new Font("Meiryo", Font.BOLD, 18));
        turnLabel.setOpaque(true);
        turnLabel.setForeground(Color.BLACK);

        JLabel alertLabel = new JLabel("保存は1ターン以上進行後に可能です");
        alertLabel.setFont(new Font("Meiryo", Font.BOLD, 10));
        alertLabel.setOpaque(true);
        alertLabel.setForeground(Color.RED);

        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        alertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnPanel.add(turnLabel);
        turnPanel.add(alertLabel);

        // Font setup
        Font jpFont = new Font("Meiryo", Font.PLAIN, 12);
        Font jpBoldFont = new Font("Meiryo", Font.BOLD, 12);
        Font activeFont = new Font("Meiryo", Font.BOLD, 14);

        logArea.setFont(jpFont);
        inputText.setFont(jpFont);
        submitButton.setFont(jpBoldFont);
        startButton.setFont(activeFont);
        loadButton.setFont(activeFont);
        multiButton.setFont(activeFont);
        saveButton.setFont(jpBoldFont);
        retryButton.setFont(jpBoldFont);
        exitButton.setFont(jpBoldFont);

        // ── HP/MP 바 공통 설정
        configureBar(playerHpBar, Color.BLUE);
        configureBar(enemyHpBar, Color.RED);
        configureBar(playerMpBar, Color.CYAN);     // MP 색상
        configureBar(enemyMpBar, Color.MAGENTA);   // MP 색상

        // 좌/우 스택: HP 위, MP 아래
        JPanel leftStack = new JPanel();
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));
        leftStack.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));
        JLabel leftHpLabel = new JLabel("自分 HP");
        JLabel leftMpLabel = new JLabel("自分 MP");
        leftHpLabel.setFont(jpBoldFont); // Font 적용
        leftMpLabel.setFont(jpBoldFont); // Font 적용
        leftStack.add(leftHpLabel);
        leftStack.add(playerHpBar);
        leftStack.add(Box.createRigidArea(new Dimension(0, 4)));
        leftStack.add(leftMpLabel);
        leftStack.add(playerMpBar);
        leftStack.setPreferredSize(new Dimension(240, 60));

        JPanel rightStack = new JPanel();
        rightStack.setLayout(new BoxLayout(rightStack, BoxLayout.Y_AXIS));
        rightStack.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        JLabel rightHpLabel = new JLabel("敵 HP");
        JLabel rightMpLabel = new JLabel("敵 MP");
        rightHpLabel.setFont(jpBoldFont); // Font 적용
        rightMpLabel.setFont(jpBoldFont); // Font 적용
        rightStack.add(rightHpLabel);
        rightStack.add(enemyHpBar);
        rightStack.add(Box.createRigidArea(new Dimension(0, 4)));
        rightStack.add(rightMpLabel);
        rightStack.add(enemyMpBar);
        rightStack.setPreferredSize(new Dimension(240, 60));

        // 상단 상태 패널
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(leftStack, BorderLayout.WEST);
        statusPanel.add(turnPanel, BorderLayout.CENTER);
        statusPanel.add(rightStack, BorderLayout.EAST);
        statusPanel.setPreferredSize(new Dimension(200, 90)); // MP 추가로 높이 확보
        gamePanel.add(statusPanel, BorderLayout.NORTH);

        //저장, 불러오기, 재도전, 끝내기 버튼
        JPanel saveButtonPanel = new JPanel();
        saveButtonPanel.setLayout(new BoxLayout(saveButtonPanel, BoxLayout.Y_AXIS)); // 수직 정렬

        // 버튼 크기 고정
        Dimension buttonsSize = new Dimension(100, 30);
        saveButton.setMaximumSize(buttonsSize);
        retryButton.setMaximumSize(buttonsSize);
        exitButton.setMaximumSize(buttonSize);

        saveButton.setBackground(Color.LIGHT_GRAY); // 배경색 변경
        saveButton.setOpaque(true);                 // 배경색 보이게 설정
        retryButton.setBackground(Color.LIGHT_GRAY); // 배경색 변경
        retryButton.setOpaque(true);                 // 배경색 보이게 설정
        exitButton.setBackground(Color.LIGHT_GRAY);
        exitButton.setOpaque(true);

        // 위로 몰기 위해 빈 공간 추가 없이 그냥 add
        saveButtonPanel.add(saveButton);
        saveButtonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 간격
        saveButtonPanel.add(retryButton);
        saveButtonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        saveButtonPanel.add(exitButton);

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

    // 공통 바 설정 함수
    private void configureBar(JProgressBar bar, Color fg) {
        bar.setMinimum(0);
        bar.setStringPainted(true);
        bar.setForeground(fg);
        bar.setPreferredSize(new Dimension(220, 18));
        bar.setFont(new Font("Meiryo", Font.BOLD, 12)); // Font 적용
    }

    public void append(String text) {
        logArea.append(text + "\n");
    }

    public JTextField getInputText() {
        return inputText;
    }

    public void showMenuPanel() {
        System.out.println("카드전환");
        cardLayout.show(mainPanel, "menu");
    }

    public void showGamePanel() {
        System.out.println("카드전환");
        cardLayout.show(mainPanel, "game");
    }
}
