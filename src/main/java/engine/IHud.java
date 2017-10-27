package engine;

import engine.items.GameItem;

/**
 * Created by IceEye on 2017-09-16.
 */

public interface IHud {

    GameItem[] getGameItems();

    default void cleanup() {
        GameItem[] gameItems = getGameItems();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}