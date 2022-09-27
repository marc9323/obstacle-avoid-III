package com.staticvoid.obstacle.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.staticvoid.obstacle.config.GameConfig;

public class PlayerSprite extends GameSpriteBase {

    // constructor
    public PlayerSprite(TextureRegion region) {
        super(region, GameConfig.PLAYER_BOUNDS_RADIUS);
        // size, width and height are the same, SIZE
        setSize(GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
    }

    // == public methods
//    public void drawDebug(ShapeRenderer renderer) {
//        renderer.x(bounds.x, bounds.y, 0.1f);
//        renderer.circle(bounds.x, bounds.y, bounds.radius, 30);
//    }

}
