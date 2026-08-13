package dino.ui;

import dino.shared.GameState;
import dino.shared.GameView;
import dino.shared.Rect;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SwingGameView implements GameView {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 320;
    private static final int GROUND_Y = 260;

    private final JFrame frame;
    private final GamePanel panel;
    private final BufferedImage dinosaurImage;
    private final BufferedImage[] cactusImages;

    private Runnable jumpHandler = () -> { };
    private Runnable restartHandler = () -> { };
    private GameState latestState;

    public SwingGameView() {
        dinosaurImage = loadImage("assets/dino.png");
        cactusImages = new BufferedImage[] {
                loadImage("assets/cactus.png"),
                loadImage("assets/cactus2.png")
        };

        panel = new GamePanel();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setBackground(new Color(250, 250, 250));
        panel.setFocusable(true);

        frame = new JFrame("Tiny Java Dino");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        installInputHandlers();

        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }

    @Override
    public void render(GameState state) {
        latestState = state;
        panel.repaint();
    }

    @Override
    public void setJumpHandler(Runnable handler) {
        jumpHandler = handler;
    }

    @Override
    public void setRestartHandler(Runnable handler) {
        restartHandler = handler;
    }

    private void installInputHandlers() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_SPACE) {
                    handleAction();
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                panel.requestFocusInWindow();
                handleAction();
            }
        });
    }

    private void handleAction() {
        if (latestState != null && latestState.gameOver()) {
            restartHandler.run();
        } else {
            jumpHandler.run();
        }
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawScene(g);
            } finally {
                g.dispose();
            }
        }
    }

    private void drawScene(Graphics2D g) {
        drawGround(g);

        if (latestState == null) {
            return;
        }

        // The view consumes game state. It does not decide behaviour or collisions.
        drawDinosaur(g, latestState.dinosaur());
        for (int i = 0; i < latestState.cactuses().size(); i++) {
            drawCactus(g, latestState.cactuses().get(i), i);
        }
        drawScore(g, latestState.score());

        if (latestState.gameOver()) {
            drawGameOver(g);
        }
    }

    private void drawGround(Graphics2D g) {
        g.setColor(new Color(90, 90, 90));
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, GROUND_Y, WIDTH, GROUND_Y);

        g.setColor(new Color(185, 185, 185));
        for (int x = 20; x < WIDTH; x += 48) {
            g.drawLine(x, GROUND_Y + 9, x + 16, GROUND_Y + 9);
        }
    }

    private void drawDinosaur(Graphics2D g, Rect dinosaur) {
        if (dinosaurImage != null) {
            drawImage(g, dinosaurImage, dinosaur);
            return;
        }

        int x = (int) Math.round(dinosaur.x());
        int y = (int) Math.round(dinosaur.y());
        int width = (int) Math.round(dinosaur.width());
        int height = (int) Math.round(dinosaur.height());

        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(x + 10, y + 4, width - 10, height - 16, 6, 6);
        g.fillRect(x, y + 20, 18, 20);
        g.fillRect(x + 8, y + height - 8, 8, 8);
        g.fillRect(x + 26, y + height - 8, 8, 8);
        g.fillRect(x + width - 3, y + 11, 11, 7);

        g.setColor(Color.WHITE);
        g.fillOval(x + width - 9, y + 10, 4, 4);
    }

    private void drawCactus(Graphics2D g, Rect cactus, int index) {
        BufferedImage cactusImage = cactusImages[index % cactusImages.length];
        if (cactusImage != null) {
            drawImage(g, cactusImage, cactus);
            return;
        }

        int x = (int) Math.round(cactus.x());
        int y = (int) Math.round(cactus.y());
        int width = (int) Math.round(cactus.width());
        int height = (int) Math.round(cactus.height());

        g.setColor(new Color(35, 135, 75));
        int trunkWidth = Math.max(8, width / 2);
        int trunkX = x + (width - trunkWidth) / 2;
        g.fillRoundRect(trunkX, y, trunkWidth, height, 8, 8);

        int armWidth = Math.max(6, width / 4);
        int armHeight = Math.max(14, height / 3);
        g.fillRoundRect(x, y + height / 3, armWidth, armHeight, 6, 6);
        g.fillRoundRect(x, y + height / 3, width / 2, armWidth, 6, 6);
        g.fillRoundRect(x + width - armWidth, y + height / 4, armWidth, armHeight, 6, 6);
        g.fillRoundRect(x + width / 2, y + height / 4, width / 2, armWidth, 6, 6);
    }

    private void drawImage(Graphics2D g, Image image, Rect bounds) {
        int x = (int) Math.round(bounds.x());
        int y = (int) Math.round(bounds.y());
        int width = (int) Math.round(bounds.width());
        int height = (int) Math.round(bounds.height());
        g.drawImage(image, x, y, width, height, null);
    }

    private void drawScore(Graphics2D g, int score) {
        g.setColor(new Color(55, 55, 55));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        g.drawString("Score " + score, WIDTH - 145, 32);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 220));
        g.fillRoundRect(240, 92, 320, 100, 8, 8);

        g.setColor(new Color(50, 50, 50));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        drawCentered(g, "Game Over", 138);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        drawCentered(g, "Press Space or click to restart", 166);
    }

    private void drawCentered(Graphics2D g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private BufferedImage loadImage(String path) {
        Path assetPath = findAsset(path);
        if (assetPath == null) {
            return null;
        }

        try {
            return ImageIO.read(assetPath.toFile());
        } catch (IOException exception) {
            return null;
        }
    }

    private Path findAsset(String path) {
        Path requestedPath = Paths.get(path);
        if (Files.isRegularFile(requestedPath)) {
            return requestedPath;
        }

        Path directory = Paths.get("").toAbsolutePath();
        while (directory != null) {
            Path candidate = directory.resolve(path);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }

            directory = directory.getParent();
        }

        return null;
    }
}
