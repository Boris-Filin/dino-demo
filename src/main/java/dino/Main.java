package dino;

import dino.logic.DinoGame;
import dino.shared.Game;
import dino.shared.GameState;
import dino.shared.GameView;
import dino.ui.SwingGameView;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
    private static final int FRAME_DELAY_MILLIS = 16;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new DinoGame();
            GameView view = new SwingGameView();

            view.setJumpHandler(game::jump);
            view.setRestartHandler(game::restart);

            startGameLoop(game, view);
        });
    }

    private static void startGameLoop(Game game, GameView view) {
        final long[] previousFrameNanos = {System.nanoTime()};

        Timer timer = new Timer(FRAME_DELAY_MILLIS, event -> {
            long now = System.nanoTime();
            double deltaSeconds = (now - previousFrameNanos[0]) / 1_000_000_000.0;
            previousFrameNanos[0] = now;

            game.update(deltaSeconds);
            GameState state = game.getState();
            view.render(state);
        });

        timer.start();
    }
}
