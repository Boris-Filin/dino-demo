package dino.logic;

import dino.shared.GameState;
import dino.shared.Rect;

import java.util.List;

public class DinoGameTest {
    public static void main(String[] args) {
        startsWithExpectedInitialState();
        jumpMovesDinosaurUpAndFallsBackToGround();
        scoreIncreasesAsTimePasses();
        collisionEndsGameAndFreezesUpdates();
        restartResetsGameAfterCollision();
        gameStateProtectsCactusListFromMutation();

        System.out.println("DinoGameTest passed");
    }

    private static void startsWithExpectedInitialState() {
        DinoGame game = new DinoGame();
        GameState state = game.getState();

        assertRectEquals(new Rect(80, 212, 48, 48), state.dinosaur());
        assertEquals(3, state.cactuses().size(), "initial cactus count");
        assertEquals(0, state.score(), "initial score");
        assertFalse(state.gameOver(), "initial gameOver");

        for (Rect cactus : state.cactuses()) {
            assertEquals(206.0, cactus.y(), "cactus y");
            assertEquals(26.0, cactus.width(), "cactus width");
            assertEquals(54.0, cactus.height(), "cactus height");
        }
    }

    private static void jumpMovesDinosaurUpAndFallsBackToGround() {
        DinoGame game = new DinoGame();

        game.jump();
        game.update(0.05);
        assertTrue(game.getState().dinosaur().y() < 212, "jump should move dinosaur above the ground");

        for (int i = 0; i < 40; i++) {
            game.update(0.05);
        }

        assertEquals(212.0, game.getState().dinosaur().y(), "dinosaur returns to ground");
    }

    private static void scoreIncreasesAsTimePasses() {
        DinoGame game = new DinoGame();

        for (int i = 0; i < 20; i++) {
            game.update(0.05);
        }

        assertTrue(game.getState().score() > 0, "score should increase during active play");
    }

    private static void collisionEndsGameAndFreezesUpdates() {
        DinoGame game = new DinoGame();

        runUntilGameOver(game);
        GameState gameOverState = game.getState();

        assertTrue(gameOverState.gameOver(), "collision should end the game");

        game.update(1.0);
        GameState laterState = game.getState();

        assertEquals(gameOverState.score(), laterState.score(), "score should freeze after game over");
        assertRectEquals(gameOverState.dinosaur(), laterState.dinosaur());
        assertRectListEquals(gameOverState.cactuses(), laterState.cactuses());
    }

    private static void restartResetsGameAfterCollision() {
        DinoGame game = new DinoGame();

        runUntilGameOver(game);
        game.restart();
        GameState state = game.getState();

        assertFalse(state.gameOver(), "restart clears gameOver");
        assertEquals(0, state.score(), "restart clears score");
        assertRectEquals(new Rect(80, 212, 48, 48), state.dinosaur());
        assertEquals(3, state.cactuses().size(), "restart cactus count");
        assertTrue(state.cactuses().get(0).x() >= 520, "restart places first cactus ahead of dinosaur");
    }

    private static void gameStateProtectsCactusListFromMutation() {
        DinoGame game = new DinoGame();
        List<Rect> cactuses = game.getState().cactuses();

        assertThrowsUnsupportedOperation(() -> cactuses.add(new Rect(0, 0, 1, 1)));
    }

    private static void runUntilGameOver(DinoGame game) {
        for (int i = 0; i < 120 && !game.getState().gameOver(); i++) {
            game.update(0.05);
        }
    }

    private static void assertRectEquals(Rect expected, Rect actual) {
        assertEquals(expected.x(), actual.x(), "rect x");
        assertEquals(expected.y(), actual.y(), "rect y");
        assertEquals(expected.width(), actual.width(), "rect width");
        assertEquals(expected.height(), actual.height(), "rect height");
    }

    private static void assertRectListEquals(List<Rect> expected, List<Rect> actual) {
        assertEquals(expected.size(), actual.size(), "rect list size");
        for (int i = 0; i < expected.size(); i++) {
            assertRectEquals(expected.get(i), actual.get(i));
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        double tolerance = 0.000001;
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean value, String label) {
        if (!value) {
            throw new AssertionError(label);
        }
    }

    private static void assertFalse(boolean value, String label) {
        if (value) {
            throw new AssertionError(label);
        }
    }

    private static void assertThrowsUnsupportedOperation(Runnable action) {
        try {
            action.run();
        } catch (UnsupportedOperationException expected) {
            return;
        }

        throw new AssertionError("expected UnsupportedOperationException");
    }
}
