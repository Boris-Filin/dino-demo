package dino.logic;

import dino.shared.Game;
import dino.shared.GameState;
import dino.shared.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DinoGame implements Game {
    private static final double WORLD_WIDTH = 800;
    private static final double GROUND_Y = 260;

    private static final double DINO_X = 80;
    private static final double DINO_WIDTH = 48;
    private static final double DINO_HEIGHT = 48;
    private static final double DINO_GROUND_Y = GROUND_Y - DINO_HEIGHT;

    private static final double GRAVITY = 1700;
    private static final double JUMP_VELOCITY = -650;

    private static final double CACTUS_WIDTH = 26;
    private static final double CACTUS_HEIGHT = 54;
    private static final double BASE_CACTUS_SPEED = 260;
    private static final double MIN_CACTUS_GAP = 260;
    private static final double EXTRA_CACTUS_GAP = 170;
    private static final int CACTUS_COUNT = 3;

    private final Random random = new Random();
    private final List<Rect> cactuses = new ArrayList<>();

    private double dinosaurY;
    private double verticalVelocity;
    private int score;
    private double scoreProgress;
    private boolean gameOver;

    public DinoGame() {
        restart();
    }

    @Override
    public void update(double deltaSeconds) {
        if (gameOver) {
            return;
        }

        double elapsed = Math.min(deltaSeconds, 0.05);
        updateDinosaur(elapsed);
        updateCactuses(elapsed);
        updateScore(elapsed);

        if (hasCollision()) {
            gameOver = true;
        }
    }

    @Override
    public void jump() {
        if (gameOver) {
            return;
        }

        if (isOnGround()) {
            verticalVelocity = JUMP_VELOCITY;
        }
    }

    @Override
    public void restart() {
        dinosaurY = DINO_GROUND_Y;
        verticalVelocity = 0;
        score = 0;
        scoreProgress = 0;
        gameOver = false;

        cactuses.clear();
        double x = 520;
        for (int i = 0; i < CACTUS_COUNT; i++) {
            cactuses.add(new Rect(x, GROUND_Y - CACTUS_HEIGHT, CACTUS_WIDTH, CACTUS_HEIGHT));
            x += nextCactusGap();
        }
    }

    @Override
    public GameState getState() {
        return new GameState(dinosaurBounds(), cactuses, score, gameOver);
    }

    private void updateDinosaur(double deltaSeconds) {
        verticalVelocity += GRAVITY * deltaSeconds;
        dinosaurY += verticalVelocity * deltaSeconds;

        if (dinosaurY > DINO_GROUND_Y) {
            dinosaurY = DINO_GROUND_Y;
            verticalVelocity = 0;
        }
    }

    private void updateCactuses(double deltaSeconds) {
        double speed = BASE_CACTUS_SPEED + Math.min(score * 0.08, 140);

        for (int i = 0; i < cactuses.size(); i++) {
            Rect cactus = cactuses.get(i);
            cactuses.set(i, new Rect(
                    cactus.x() - speed * deltaSeconds,
                    cactus.y(),
                    cactus.width(),
                    cactus.height()
            ));
        }

        recycleOffscreenCactuses();
    }

    private void recycleOffscreenCactuses() {
        for (int i = 0; i < cactuses.size(); i++) {
            Rect cactus = cactuses.get(i);
            if (cactus.x() + cactus.width() < 0) {
                double nextX = rightmostCactusX() + nextCactusGap();
                cactuses.set(i, new Rect(nextX, cactus.y(), cactus.width(), cactus.height()));
            }
        }
    }

    private double rightmostCactusX() {
        return cactuses.stream()
                .max(Comparator.comparingDouble(Rect::x))
                .map(Rect::x)
                .orElse(WORLD_WIDTH);
    }

    private double nextCactusGap() {
        return MIN_CACTUS_GAP + random.nextDouble(EXTRA_CACTUS_GAP);
    }

    private void updateScore(double deltaSeconds) {
        scoreProgress += deltaSeconds * 12;
        int earnedPoints = (int) scoreProgress;
        if (earnedPoints > 0) {
            score += earnedPoints;
            scoreProgress -= earnedPoints;
        }
    }

    private boolean hasCollision() {
        Rect dinosaur = dinosaurBounds();

        // Game logic knows collision bounds, but not how either shape is drawn.
        for (Rect cactus : cactuses) {
            if (dinosaur.intersects(cactus)) {
                return true;
            }
        }

        return false;
    }

    private boolean isOnGround() {
        return dinosaurY >= DINO_GROUND_Y;
    }

    private Rect dinosaurBounds() {
        return new Rect(DINO_X, dinosaurY, DINO_WIDTH, DINO_HEIGHT);
    }
}
