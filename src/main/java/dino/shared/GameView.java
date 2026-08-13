package dino.shared;

public interface GameView {
    void render(GameState state);

    void setJumpHandler(Runnable handler);

    void setRestartHandler(Runnable handler);
}
