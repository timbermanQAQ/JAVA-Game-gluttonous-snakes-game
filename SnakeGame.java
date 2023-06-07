package test;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SnakeGame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private boolean gameStarted = false;
    // 定义游戏区域的大小
    private final int WIDTH = 640;
    private final int HEIGHT = 640;

    // 定义贪吃蛇的初始位置和大小
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 30;
    private int[] x = new int[ALL_DOTS];
    private int[] y = new int[ALL_DOTS];
    private int dots;
    private int apple_x;
    private int apple_y;
    private int score = 0;

    // 定义贪吃蛇的移动方向
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    // 定义游戏是否结束
    private boolean inGame = true;

    // 定义计时器
    private Timer timer;


    private void loadAudio() {
        try {
            String temp="C:/Users/timberman/Desktop/disco.wav";
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(temp));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error: audio file not found or could not be loaded.");
        }
    }
    public SnakeGame() {
        initGame();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT && !rightDirection) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                } else if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                } else if (key == KeyEvent.VK_UP && !downDirection) {
                    upDirection = true;
                    leftDirection = false;
                    rightDirection = false;
                } else if (key == KeyEvent.VK_DOWN && !upDirection) {
                    downDirection = true;
                    leftDirection = false;
                    rightDirection = false;
                }
            }
        });
        setFocusable(true);
    }

    public void initGame() {
        loadAudio();
        // Initialize game area
        setTitle("SnakeGame");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        getContentPane().setBackground(Color.black);

        // Add start screen
        JLabel startLabel = new JLabel("Press the space bar to start the game");
        startLabel.setForeground(Color.white);
        startLabel.setBackground(Color.black);
        startLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        startLabel.setHorizontalAlignment(JLabel.CENTER);
        startLabel.setVerticalAlignment(JLabel.CENTER);
        add(startLabel);

        // Initialize snake and apple positions
        dots = 3;
        for (int i = 0; i < dots; i++) {
            x[i] = 50 - i * DOT_SIZE;
            y[i] = 50;
        }
        locateApple();

        // Initialize timer
        timer = new Timer(140, this);

        // Add key listener to start game
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_SPACE && !gameStarted) {
                    // Remove start screen
                    remove(startLabel);
                    gameStarted = true;
                    // Start timer
                    timer.start();
                }
            }
        });

        score = 0;
    }

    public void locateApple() {
        // 随机生成苹果的位置
        int r = (int) (Math.random() * RAND_POS);
        apple_x = r * DOT_SIZE;

        r = (int) (Math.random() * (RAND_POS/2)); // limit apple to top half of game area
        apple_y = r * DOT_SIZE;
    }
    public void checkApple() {
        // 检查贪吃蛇是否吃到了苹果
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
            score++;
            setTitle("SnakeGame - Score: " + score);
        }
    }

    public void checkCollision() {
        // 检查贪吃蛇是否碰到了边界或自己的身体
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (y[0] >= HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    public void move() {
        // 移动贪吃蛇
        for (int i = dots; i > 0; i--) {
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    public void actionPerformed(ActionEvent e) {
        // 计时器触发事件
        if (inGame) {
            checkApple();
            checkCollision();
            move();
            repaint(); // 重绘游戏区域
        }
    }

    public void paint(Graphics g) {

        // 绘制游戏区域
        super.paint(g);

        if (inGame) {
            g.setColor(Color.red);
            g.fillOval(apple_x, apple_y, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < dots; i++) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
            }
            g.setColor(Color.black);
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        // 游戏结束
        String msg = "GAME OVER";
        Font small = new Font("Helvetica", Font.BOLD, 40);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (WIDTH - metr.stringWidth(msg)) / 2, HEIGHT / 2);

        String scoreMsg = "Final Score: " + score;
        Font smallScore = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics scoreMetr = getFontMetrics(smallScore);
        g.setFont(smallScore);
        g.drawString(scoreMsg, (WIDTH - scoreMetr.stringWidth(scoreMsg)) / 2, HEIGHT / 2 + 50);

    }


    public static void main(String[] args) {
        new SnakeGame();
    }
}
