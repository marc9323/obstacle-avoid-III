package com.staticvoid.obstacle.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

// Both Player and Obstacle have bounds and texture
public abstract class GameSpriteBase extends Sprite {

    // == attributes
    protected Circle bounds;

    // we used Circle for custom bounds
    // alternatively for rectangular collision detection you can use -->
//    @Override
//    public Rectangle getBoundingRectangle() {
//        return super.getBoundingRectangle();
//    }

    // == Constructors
    public GameSpriteBase(TextureRegion region, float boundsRadius) {
        super(region);
        bounds = new Circle(getX(), getY(), boundsRadius);
    }

    // == public methods
    public Circle getBounds() {
        return bounds;
    }

    // to ensure updateBounds() is called override setPosition of Sprite class
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateBounds();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateBounds();
    }

    // bounds MUST be updated whenever position or size changes
    public void updateBounds() {
        // address NullPointerException
        if (bounds == null) {
            //  bounds = new Circle... or,
            return;
        }
        // remember we are shifting right and up to account
        // for the mismatched circle and texture origin points
        float halfWidth = getWidth() / 2f; // getWidth/height defined in Sprite
        float halfHeight = getHeight() / 2f;

        // getX and getY() also defined in Sprite
        bounds.setPosition(getX() + halfWidth, getY() + halfHeight);
    }

    public void drawDebug(ShapeRenderer renderer) {
        renderer.x(bounds.x, bounds.y, 0.1f);
        renderer.circle(bounds.x, bounds.y, bounds.radius, 30);
    }

    // better to update the bounds here, especially for later projects
    // or expansions where, for example, multiple types of obstacle or of sprite
    // are involved.
    @Override
    public void setX(float x) {
        super.setX(x);
        updateBounds();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        updateBounds();
    }
}
