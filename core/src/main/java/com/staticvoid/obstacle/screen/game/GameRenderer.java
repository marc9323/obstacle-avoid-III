package com.staticvoid.obstacle.screen.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.staticvoid.obstacle.assets.AssetDescriptors;
import com.staticvoid.obstacle.assets.RegionNames;
import com.staticvoid.obstacle.config.GameConfig;
import com.staticvoid.obstacle.entity.Background;
import com.staticvoid.obstacle.entity.ObstacleSprite;
import com.staticvoid.obstacle.entity.PlayerSprite;
import com.staticvoid.obstacle.util.GdxUtils;
import com.staticvoid.obstacle.util.ViewportUtils;
import com.staticvoid.obstacle.util.debug.DebugCameraController;

public class GameRenderer implements Disposable {

    private static final Logger log = new Logger(GameRenderer.class.getName(), Application.LOG_DEBUG);

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    // hud requires another camera and another viewport
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private DebugCameraController debugCameraController;

    private final GameController controller;
    private final AssetManager assetManager;
    private final SpriteBatch batch;

    private TextureRegion backgroundRegion;

//    private TextureRegion playerRegion;
//    private TextureRegion obstacleRegion;
//    private TextureRegion backgroundRegion;

    public GameRenderer(SpriteBatch batch, AssetManager assetManager, GameController controller) {
        this.batch = batch;
        this.assetManager = assetManager;
        this.controller = controller;
        init();
    }

    // == init ==
    private void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudCamera);
        font = assetManager.get(AssetDescriptors.FONT);


        // create debug camera controller
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X, GameConfig.WORLD_CENTER_Y);

        // TextureAtlas gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);

//        playerRegion = gamePlayAtlas.findRegion(RegionNames.PLAYER);
//        obstacleRegion = gamePlayAtlas.findRegion(RegionNames.OBSTACLE);
//        backgroundRegion = gamePlayAtlas.findRegion(RegionNames.BACKGROUND);

        TextureAtlas gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
        backgroundRegion = gamePlayAtlas.findRegion(RegionNames.BACKGROUND);
    }

    // == public methods ==
    public void render(float delta) {
        // not wrapping inside alive cuz we want to be able to control camera even when there is game over
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);

        if (Gdx.input.isTouched() && !controller.isGameOver()) {
            Vector2 screenTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector2 worldTouch = viewport.unproject(new Vector2(screenTouch));

            System.out.println("screenTouch= " + screenTouch);
            System.out.println("worldTouch= " + worldTouch);

            PlayerSprite player = controller.getPlayer();
            worldTouch.x = MathUtils.clamp(worldTouch.x, 0, GameConfig.WORLD_WIDTH - player.getWidth());
            player.setX(worldTouch.x);
        }

        // clear screen
        GdxUtils.clearScreen();

        renderGamePlay();

        // render ui/hud
        renderUi();

        // render debug graphics
        renderDebug();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    // == private methods ==
    private void renderGamePlay() {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // draw background
        Background background = controller.getBackground();
        batch.draw(backgroundRegion,
                background.getX(), background.getY(),
                background.getWidth(), background.getHeight());

        // alternatively, background could be generated by EntityFactory
        // or simply draw the background region texture at zero with world width and height

        // draw background
//        Background background = controller.getBackground();
//        batch.draw(backgroundRegion,
//                background.getX(), background.getY(),
//                background.getWidth(), background.getHeight()
//        );

        // draw player
        PlayerSprite player = controller.getPlayer();
        player.draw(batch);
        // NOTE:  Sprite knows how to draw itself
        // always, if using Sprite, have the sprite draw itself as above
        // otherwise possible to fail to take into account properties baked into
        // Sprite such as origin, scale, etc. etc.

//        batch.draw(player,
//                player.getX(), player.getY(),
//                player.getWidth(), player.getHeight()
//        );

        // draw obstacles
        for (ObstacleSprite obstacle : controller.getObstacles()) {
            obstacle.draw(batch);
//            batch.draw(obstacleRegion,
//                    obstacle.getX(), obstacle.getY(),
//                    obstacle.getWidth(), obstacle.getHeight()
            //  );
        }

        batch.end();
    }

    private void renderUi() {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String livesText = "LIVES: " + controller.getLives();
        layout.setText(font, livesText);

        font.draw(batch, livesText,
                20,
                GameConfig.HUD_HEIGHT - layout.height
        );

        String scoreText = "SCORE: " + controller.getDisplayScore();
        layout.setText(font, scoreText);

        font.draw(batch, scoreText,
                GameConfig.HUD_WIDTH - layout.width - 20,
                GameConfig.HUD_HEIGHT - layout.height
        );

        batch.end();
    }

    private void renderDebug() {
        viewport.apply();
        // draw red round PlayerSprite
        Color oldColor = renderer.getColor().cpy();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        renderer.setColor(oldColor);

        ViewportUtils.drawGrid(viewport, renderer);
    }

    private void drawDebug() {
        renderer.setColor(Color.RED);
        PlayerSprite player = controller.getPlayer();
        player.drawDebug(renderer);
//
        Array<ObstacleSprite> obstacles = controller.getObstacles();
//
        for (ObstacleSprite obstacle : obstacles) {
            obstacle.drawDebug(renderer);
        }
    }
}



/*
NOTES:
Optimal solution would be to create A Model for the game world and
 instead of passing controller to renderer pass in the model.
 This would completely decouple the renderer and the controller code.

 See Video 95 for brief overview of MVC solution.

 TextureAtlas yields 50% reduction in Texture swap render calls! Use it!
 */