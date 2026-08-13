package dino.shared;

import java.util.List;

public record GameState(
        Rect dinosaur,
        List<Rect> cactuses,
        int score,
        boolean gameOver
) {
    public GameState {
        cactuses = List.copyOf(cactuses);
    }
}
