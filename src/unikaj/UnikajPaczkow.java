package unikaj;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class UnikajPaczkow {

    public static void main(String[] args) {
        // Uruchomienie aplikacji w wątku zdarzeń Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Unikaj Pączków - Wersja Java");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLayout(new BorderLayout());

            // Panel główny, który będzie zawierał panel gry i wizualizacji
            JPanel mainContainer = new JPanel();
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.X_AXIS));
            mainContainer.setBackground(new Color(0x1a1a1a));
            mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Panel gry
            GamePanel gamePanel = new GamePanel();
            
            // Panel wizualizacji
            VisPanel visPanel = new VisPanel(gamePanel);
            
            // Połączenie paneli
            gamePanel.setVisPanel(visPanel);

            // Dodanie paneli do kontenera
            mainContainer.add(gamePanel.getLayeredPane()); // Używamy JLayeredPane dla przycisku
            mainContainer.add(Box.createHorizontalStrut(20)); // Odstęp
            mainContainer.add(visPanel);

            frame.add(mainContainer, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null); // Wyśrodkowanie okna
            frame.setVisible(true);

            gamePanel.startGame(); // Rozpoczęcie pętli gry
        });
    }
}

/**
 * Panel gry, który zarządza całą logiką i rysowaniem.
 */
class GamePanel extends JPanel implements Runnable {

    // --- Konfiguracja Gry ---
    private static final String GAME_MODE = "PLAYER"; // Dostępne tryby: 'PLAYER', 'PC', 'PLC'
    private static final int CANVAS_WIDTH = 400;
    private static final int CANVAS_HEIGHT = 600;

    private static final int PLAYER_RADIUS = 4;
    private static final int DONUT_RADIUS = 20;
    private static final double DONUT_SPEED_PPS = 300;
    private static final int DONUTS_PER_MINUTE = 550;
    private static final int INITIAL_LIVES = 5000;
    private static final int MAX_SCORE_DIFFERENCE = 500;
    private static final double LIFE_REGEN_SECONDS = 100;
    private static final double PLAYER_SPEED_PPS = 2000;
    private static final int COLLISION_Y_THRESHOLD_PX = PLAYER_RADIUS + DONUT_RADIUS - 5;

    // --- Konfiguracja PC ---
    private static final int PC_REACTION_DISTANCE = 400;
    private static final int PC_SAFE_BUFFER_PX = 20;

    // --- Konfiguracja Wizualizacji / PLC ---
    private static final boolean VIS_ENABLED = true;
     static final int VIS_SENSOR_COUNT = 21;
    private static final int PLC_MIN_SAFE_DISTANCE_PX = 80;

    // --- Stan Gry ---
    Player player;
    List<Donut> donuts;
    int lives;
    Score score;
    boolean gameOver;
    double lifeRegenTimer;
    int donutIdCounter;

    private Thread gameThread;
    private volatile boolean running = false;
    private final Random random = new Random();
    private double donutSpawnTimer = 0;
    private double donutsSpawnInterval = 60.0 / DONUTS_PER_MINUTE;
    private JLayeredPane layeredPane;
    private JButton restartButton;
    private VisPanel visPanel;

    // --- Konstruktor ---
    public GamePanel() {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        setBackground(new Color(0x4169E1));
        setFocusable(true);

        setupUI();
    }

    private void setupUI() {
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        
        this.setBounds(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        layeredPane.add(this, JLayeredPane.DEFAULT_LAYER);

        restartButton = new JButton("Zagraj Ponownie");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setBackground(new Color(0x4CAF50));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> resetGame());

        Dimension btnSize = restartButton.getPreferredSize();
        restartButton.setBounds(CANVAS_WIDTH / 2 - btnSize.width / 2, CANVAS_HEIGHT / 2 - btnSize.height / 2, btnSize.width, btnSize.height);
        layeredPane.add(restartButton, JLayeredPane.PALETTE_LAYER);
    }
    
    public JLayeredPane getLayeredPane() {
        return layeredPane;
    }
    
    public void setVisPanel(VisPanel visPanel) {
        this.visPanel = visPanel;
    }

    // --- Inicjalizacja Gry ---
    public void initGame() {
        player = new Player(CANVAS_WIDTH / 2.0, CANVAS_HEIGHT - 60, PLAYER_RADIUS);
        donuts = new CopyOnWriteArrayList<>(); // Bezpieczna dla wątków lista
        lives = INITIAL_LIVES;
        score = new Score();
        gameOver = false;
        lifeRegenTimer = 0;
        donutIdCounter = 0;
        donutSpawnTimer = 0;
        restartButton.setVisible(false);

        // Usuwamy i dodajemy listener tylko jeśli jest to konieczne
        this.removeMouseListener(playerMouseListener);
        if ("PLAYER".equals(GAME_MODE)) {
            this.addMouseListener(playerMouseListener);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    private void resetGame() {
        initGame();
        if (!running) {
            startGame();
        }
    }

    public void startGame() {
        initGame();
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // --- Główna pętla gry ---
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0;
        double deltaTime = 0;

        while (running) {
            long now = System.nanoTime();
            deltaTime = (now - lastTime) / ns;
            lastTime = now;

            if (!gameOver) {
                update(deltaTime);
            }
            
            repaint(); // Odrysowuje GamePanel
            if (VIS_ENABLED && visPanel != null) {
                visPanel.repaint(); // Odrysowuje VisPanel
            }

            try {
                // Stała liczba klatek na sekundę (około 60 FPS)
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    // --- Aktualizacja stanu gry ---
    private void update(double deltaTime) {
        // Sterowanie AI
        if ("PC".equals(GAME_MODE)) {
            controlPrecognitivePlanner();
        } else if ("PLC".equals(GAME_MODE)) {
            controlPlc();
        }

        // Ruch gracza
        player.updateMovement();
        player.clamp(0, CANVAS_WIDTH);

        // Spawn pączków
        donutSpawnTimer += deltaTime;
        if (donutSpawnTimer >= donutsSpawnInterval) {
            spawnDonut();
            donutSpawnTimer = 0;
        }

        // Aktualizacja pączków (ruch, kolizje, punktacja)
        for (Donut donut : donuts) {
            donut.y += DONUT_SPEED_PPS * deltaTime;

            // Kolizja
            double dist = Math.hypot(player.x - donut.x, player.y - donut.y);
            if (dist - donut.radius - player.radius < 1 && player.y - donut.y > COLLISION_Y_THRESHOLD_PX) {
                lives--;
                lifeRegenTimer = 0;
                donuts.remove(donut);
                if (lives <= 0) {
                    endGame();
                }
                continue;
            }

            // Punktacja
            if (donut.y - donut.radius > player.y + player.radius && !donut.counted) {
                if (donut.x < player.x) score.left++;
                else score.right++;
                donut.counted = true;
                score.diff = Math.abs(score.left - score.right);
                if (score.diff > MAX_SCORE_DIFFERENCE) {
                    endGame();
                }
            }
        }
        
        // Usunięcie pączków poza ekranem
        donuts.removeIf(d -> d.y - d.radius > CANVAS_HEIGHT);

        // Regeneracja życia
        if (lives < INITIAL_LIVES && lives > 0) {
            lifeRegenTimer += deltaTime;
            if (lifeRegenTimer >= LIFE_REGEN_SECONDS) {
                lives++;
                lifeRegenTimer = 0;
            }
        } else {
            lifeRegenTimer = 0;
        }
    }

    private void endGame() {
        gameOver = true;
        restartButton.setVisible(true);
        // Pętla gry będzie kontynuowana, ale update nie będzie wywoływany
    }

    // --- Rysowanie ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDonuts(g2d);
        drawPlayer(g2d);
        drawUI(g2d);

        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    private void drawPlayer(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fill(new Ellipse2D.Double(player.x - player.radius, player.y - player.radius, player.radius * 2, player.radius * 2));
    }

    private void drawDonuts(Graphics2D g2d) {
        g2d.setColor(new Color(0xFFA500)); // Orange
        for (Donut donut : donuts) {
            g2d.fill(new Ellipse2D.Double(donut.x - donut.radius, donut.y - donut.radius, donut.radius * 2, donut.radius * 2));
        }
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Życia: " + lives, 10, 25);
        g2d.drawString("Minięte z lewej: " + score.left, 10, 55);
        g2d.drawString("Minięte z prawej: " + score.right, 10, 85);
        g2d.drawString("Różnica: " + score.diff, 10, 115);

        if (lives < INITIAL_LIVES && lives > 0) {
            double regenProgress = Math.min(1, lifeRegenTimer / LIFE_REGEN_SECONDS);
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRect(10, 125, 100, 10);
            g2d.setColor(new Color(0x66ff66));
            g2d.fillRect(10, 125, (int) (100 * regenProgress), 10);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.WHITE);
        String msg1 = "KONIEC GRY";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(msg1, CANVAS_WIDTH / 2 - fm.stringWidth(msg1) / 2, CANVAS_HEIGHT / 2 - 40);

        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String msg2 = "Wynik: " + (score.left + score.right) + " pączków";
        fm = g2d.getFontMetrics();
        g2d.drawString(msg2, CANVAS_WIDTH / 2 - fm.stringWidth(msg2) / 2, CANVAS_HEIGHT / 2);
    }

    // --- Logika Pomocnicza ---
    private void spawnDonut() {
        final int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            double x = random.nextDouble() * (CANVAS_WIDTH - DONUT_RADIUS * 2) + DONUT_RADIUS;
            boolean overlap = false;
            for (Donut donut : donuts) {
                if (donut.y < DONUT_RADIUS * 2 && Math.abs(x - donut.x) < DONUT_RADIUS * 2) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                donuts.add(new Donut(donutIdCounter++, x, -DONUT_RADIUS, DONUT_RADIUS));
                return;
            }
        }
    }
    
    // --- Sterowanie i Logika AI ---
    private final MouseAdapter playerMouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!gameOver) {
                player.setTarget(e.getX());
            }
        }
    };
    
    private void controlPlc() {
        Double[] sensorData = getSensorData();
        double sensorSpacing = (double) CANVAS_WIDTH / VIS_SENSOR_COUNT;
        int playerSensorWidth = (int) Math.ceil((player.radius * 2) / sensorSpacing);

        List<Gap> safeGaps = new ArrayList<>();
        Gap currentGap = null;

        for (int i = 0; i < VIS_SENSOR_COUNT; i++) {
            boolean isSensorSafe = sensorData[i] == null || sensorData[i] > PLC_MIN_SAFE_DISTANCE_PX;
            if (isSensorSafe) {
                if (currentGap == null) {
                    currentGap = new Gap(i, i);
                } else {
                    currentGap.end = i;
                }
            } else {
                if (currentGap != null) {
                    if (currentGap.end - currentGap.start + 1 >= playerSensorWidth) {
                        safeGaps.add(currentGap);
                    }
                    currentGap = null;
                }
            }
        }
        if (currentGap != null && currentGap.end - currentGap.start + 1 >= playerSensorWidth) {
            safeGaps.add(currentGap);
        }

        double idealTargetX = player.x;
        if (!safeGaps.isEmpty()) {
            boolean needsRightPoint = score.left > score.right;
            boolean needsLeftPoint = score.right > score.left;

            List<Gap> candidateGaps = new ArrayList<>(safeGaps);

            if (needsRightPoint || needsLeftPoint) {
                final boolean finalNeedsRight = needsRightPoint;
                List<Gap> balancingGaps = safeGaps.stream().filter(gap -> {
                    double gapCenterX = ((gap.start + gap.end) / 2.0 + 0.5) * sensorSpacing;
                    return finalNeedsRight ? gapCenterX < CANVAS_WIDTH / 2.0 : gapCenterX > CANVAS_WIDTH / 2.0;
                }).collect(Collectors.toList());

                if (!balancingGaps.isEmpty()) {
                    candidateGaps = balancingGaps;
                }
            }
            
            for (Gap gap : candidateGaps) {
                double gapCenterX = ((gap.start + gap.end) / 2.0 + 0.5) * sensorSpacing;
                double distanceToGap = Math.abs(player.x - gapCenterX);
                int width = gap.end - gap.start + 1;
                gap.score = distanceToGap - width * 20;
            }

            Gap bestGap = candidateGaps.stream().min(Comparator.comparingDouble(g -> g.score)).get();
            idealTargetX = ((bestGap.start + bestGap.end) / 2.0 + 0.5) * sensorSpacing;
        }

        player.setTarget(idealTargetX);
    }
    
    private Double[] getSensorData() {
        Double[] sensorData = new Double[VIS_SENSOR_COUNT];
        double sensorSpacing = (double) CANVAS_WIDTH / VIS_SENSOR_COUNT;

        for (int i = 0; i < VIS_SENSOR_COUNT; i++) {
            double sensorX = (i + 0.5) * sensorSpacing;
            for (Donut donut : donuts) {
                if (donut.y < player.y && Math.abs(sensorX - donut.x) < donut.radius) {
                    double distance = player.y - donut.y;
                    if (sensorData[i] == null || distance < sensorData[i]) {
                        sensorData[i] = distance;
                    }
                }
            }
        }
        return sensorData;
    }
    
    private void controlPrecognitivePlanner() {
        CollisionDetails standStillDetails = getPathCollisionDetails(player.x);
        Donut threat = standStillDetails.collidingDonut;
        
        // Ta logika jest uproszczona w stosunku do oryginału dla zwięzłości
        if (threat != null) {
            double sidestepLeftTarget = threat.x - DONUT_RADIUS - player.radius - PC_SAFE_BUFFER_PX;
            double sidestepRightTarget = threat.x + DONUT_RADIUS + player.radius + PC_SAFE_BUFFER_PX;

            if (getPathCollisionDetails(sidestepLeftTarget).isSafe) {
                player.setTarget(sidestepLeftTarget);
            } else if (getPathCollisionDetails(sidestepRightTarget).isSafe) {
                player.setTarget(sidestepRightTarget);
            }
        } else if (Math.abs(player.x - player.targetX) < 1) {
            // Jeśli nie ma zagrożenia i stoimy w miejscu, idź do środka
            if (getPathCollisionDetails(CANVAS_WIDTH / 2.0).isSafe) {
                player.setTarget(CANVAS_WIDTH / 2.0);
            }
        }
    }

    private CollisionDetails getPathCollisionDetails(double targetX) {
        double distance = Math.abs(targetX - player.x);
        double moveDurationMs = (distance / PLAYER_SPEED_PPS) * 1000;
        double effectiveMoveDurationMs = Math.max(moveDurationMs, 1000.0 / 60.0);
        double timeStepMs = 1000.0 / 60.0;
        int simulationSteps = (int) Math.ceil(effectiveMoveDurationMs / timeStepMs);

        for (int i = 1; i <= simulationSteps; i++) {
            double simTimeMs = i * timeStepMs;
            double progress = moveDurationMs > 0 ? Math.min(1, simTimeMs / moveDurationMs) : 0;
            double playerFutureX = player.x + (targetX - player.x) * progress;

            for (Donut donut : donuts) {
                double donutFutureY = donut.y + DONUT_SPEED_PPS * (simTimeMs / 1000.0);
                double dist = Math.hypot(playerFutureX - donut.x, player.y - donutFutureY);
                if (dist < player.radius + DONUT_RADIUS && player.y - donutFutureY > COLLISION_Y_THRESHOLD_PX) {
                    return new CollisionDetails(false, donut);
                }
            }
        }
        return new CollisionDetails(true, null);
    }
    
    // Klasy pomocnicze (wewnętrzne)
    static class Player {
        double x, y, radius;
        double targetX, startX;
        long moveStartTime;
        double moveDuration;

        Player(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.targetX = x;
        }

        void setTarget(double newTargetX) {
            if (this.targetX == newTargetX) return;
            this.targetX = newTargetX;
            this.startX = this.x;
            this.moveStartTime = System.currentTimeMillis();
            double distance = Math.abs(this.targetX - this.x);
            this.moveDuration = (distance / PLAYER_SPEED_PPS) * 1000;
        }

        void updateMovement() {
            if (moveDuration > 0 && x != targetX) {
                long elapsedTime = System.currentTimeMillis() - moveStartTime;
                double progress = elapsedTime / moveDuration;
                if (progress >= 1) {
                    x = targetX;
                    moveDuration = 0;
                } else {
                    x = startX + (targetX - startX) * progress;
                }
            }
        }
        
        void clamp(double minX, double maxX) {
            if (x - radius < minX) x = minX + radius;
            if (x + radius > maxX) x = maxX - radius;
        }
    }

    static class Donut {
        final int id;
        double x, y, radius;
        boolean counted = false;
        Donut(int id, double x, double y, double radius) {
            this.id = id; this.x = x; this.y = y; this.radius = radius;
        }
    }

    static class Score { int left, right, diff; }
    
    static class Gap {
        int start, end;
        double score;
        Gap(int start, int end) { this.start = start; this.end = end; }
    }

    static class CollisionDetails {
        final boolean isSafe;
        final Donut collidingDonut;
        CollisionDetails(boolean isSafe, Donut donut) { this.isSafe = isSafe; this.collidingDonut = donut; }
    }
}

/**
 * Panel wizualizacji, który odrysowuje dane z GamePanel.
 */
class VisPanel extends JPanel {
    private final GamePanel gamePanel;

    public VisPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gamePanel == null || gamePanel.player == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double sensorSpacing = (double) getWidth() / GamePanel.VIS_SENSOR_COUNT;

        // Rysuj siatkę sensorów
        g2d.setColor(new Color(128, 128, 128, 128));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < GamePanel.VIS_SENSOR_COUNT; i++) {
            double sensorX = (i + 0.5) * sensorSpacing;
            g2d.drawLine((int) sensorX, 0, (int) sensorX, getHeight());
        }

        // Rysuj gracza
        g2d.setColor(Color.RED);
        g2d.fillRect((int) (gamePanel.player.x - gamePanel.player.radius), (int) gamePanel.player.y, (int) (gamePanel.player.radius * 2), 3);

        // Rysuj wykryte pączki
        g2d.setColor(Color.WHITE);
        for (GamePanel.Donut donut : gamePanel.donuts) {
            if (donut.y > -donut.radius && donut.y < getHeight() + donut.radius) {
                double donutLeft = donut.x - donut.radius;
                double donutRight = donut.x + donut.radius;
                for (int i = 0; i < GamePanel.VIS_SENSOR_COUNT; i++) {
                    double sensorX = (i + 0.5) * sensorSpacing;
                    if (sensorX >= donutLeft && sensorX <= donutRight) {
                        g2d.fillRect((int) sensorX - 1, (int) donut.y - 1, 2, 2);
                    }
                }
            }
        }
    }
}