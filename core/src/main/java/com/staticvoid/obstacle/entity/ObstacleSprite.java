package com.staticvoid.obstacle.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Pool;
import com.staticvoid.obstacle.config.GameConfig;

public class ObstacleSprite extends GameSpriteBase implements Pool.Poolable {


    // defaults to medium as per enum
    private float ySpeed = GameConfig.MEDIUM_OBSTACLE_SPEED;
    private boolean hit;

    // no args constructor - which is required for ReflectionPool
    // ( as well as many other reflection heavy Java libraries )
    public ObstacleSprite(TextureRegion region) {
        super(region, GameConfig.OBSTACLE_BOUNDS_RADIUS);
        setSize(GameConfig.OBSTACLE_SIZE, GameConfig.OBSTACLE_SIZE);
    }

    // TODO:  # 154
    // Fix by adding no-args constructor and using this setter
    // alternatively, more difficult solution is to create custom pool
//    @Override
//    public void setRegion(Texture texture) {
//        super.setRegion(texture);
//    }

    public void update() {
        setY(getY() - ySpeed);
       // updateBounds();
    }

    public boolean isPlayerColliding(PlayerSprite player) {
        Circle playerBounds = player.getBounds();
        // player bounds overlaps obstacle bounds?
        boolean overlaps = Intersector.overlaps(playerBounds, getBounds());
        hit = overlaps; // > elegant than conditional
        return overlaps;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isNotHit() {
        return !hit;
    }

    public void setYSpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    @Override
    public void reset() {
        hit = false;

    }
}
