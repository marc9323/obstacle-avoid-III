package com.staticvoid.obstacle.common;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.staticvoid.obstacle.assets.AssetDescriptors;
import com.staticvoid.obstacle.assets.RegionNames;
import com.staticvoid.obstacle.config.GameConfig;
import com.staticvoid.obstacle.entity.Background;
import com.staticvoid.obstacle.entity.ObstacleSprite;
import com.staticvoid.obstacle.entity.PlayerSprite;

public class EntityFactory {

    // == attributes
    private final AssetManager assetManager;
    private TextureAtlas gamePlayAtlas;
    private TextureRegion obstacleRegion;
    private Pool<ObstacleSprite> obstaclePool;

    // constructor
    public EntityFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
        init();
    }

    // CUSTOM POOL
    private void init() {
        gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
        obstacleRegion = gamePlayAtlas.findRegion(RegionNames.OBSTACLE);
        obstaclePool = new Pool<ObstacleSprite>(40) { // pool size
            @Override
            protected ObstacleSprite newObject() {
                // use this space to get a reference and set properties if need be
                return new ObstacleSprite(obstacleRegion);
            }
        };
    }

    // == public methods
    public PlayerSprite createPlayer() {
        TextureRegion playerRegion = gamePlayAtlas.findRegion(RegionNames.PLAYER);
        return new PlayerSprite(playerRegion);
    }

    public Background createBackground() {
        Background background = new Background();
        background.setPosition(0, 0);
        background.setSize(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        return background;
    }

    public ObstacleSprite obtain() {
        ObstacleSprite obstacle = obstaclePool.obtain();
        obstacle.setRegion(obstacleRegion);
        return obstacle;
    }

    public void free(ObstacleSprite obstacle) {
        obstaclePool.free(obstacle);
    }

    public void freeAll(Array<ObstacleSprite> obstacles) {
        obstaclePool.freeAll(obstacles);
    }
}
