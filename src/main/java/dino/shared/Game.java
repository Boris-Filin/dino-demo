package dino.shared;

public interface Game {
    void update(double deltaSeconds);

    void jump();

    void restart();

    GameState getState();
}
