package dino.ui;

import dino.shared.GameState;
import dino.shared.GameView;
import dino.shared.Rect;

import javax.swing.SwingUtilities;
import java.util.List;

public class AssetPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView view = new SwingGameView();
            view.render(new GameState(
                    new Rect(80, 212, 48, 48),
                    List.of(
                            new Rect(340, 206, 26, 54),
                            new Rect(520, 206, 26, 54)
                    ),
                    42,
                    false
            ));
        });
    }
}
