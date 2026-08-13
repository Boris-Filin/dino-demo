# Tiny Java Dino

This is a deliberately small Java Swing Dino game for demonstrating clean team decomposition, small shared contracts, and object-oriented design.

```text
                 shared/
          interfaces + GameState
                /        \
               /          \
              v            v
       logic/DinoGame   ui/SwingGameView
          behaviour        rendering
               \          /
                \        /
                  Main
                  wiring
```

The team first agrees on the shared contract in `dino.shared`: `Game`, `GameView`, `GameState`, and `Rect`.

After that, one developer can implement `logic/DinoGame` while another developer implements `ui/SwingGameView`. Both sides depend on the shared interfaces and data, not on each other. This reduces coordination requirements and lowers the chance of Git merge conflicts.

`DinoGame` knows where the cactus is and whether the dinosaur collided with it, but does not know what the cactus looks like.

`SwingGameView` knows how to draw a cactus, but does not know that touching it causes game over.

## Run

1. Open the project in IntelliJ IDEA.
2. Open `src/main/java/dino/Main.java`.
3. Click the green Run button next to `main()`.

Controls:

- Space or mouse click: jump
- Space or mouse click after game over: restart

## Preview assets

Dev 2 can preview a static sample scene without waiting for the game logic:

```shell
javac -d out $(find src/main/java -name '*.java')
java -cp out dino.ui.AssetPreview
```

## Test game logic

Dev 1 can test the logic without waiting for the Swing UI:

```shell
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out dino.logic.DinoGameTest
```
