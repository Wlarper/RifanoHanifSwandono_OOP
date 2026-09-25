package com.Rifano.frontend.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject implements com.Rifano.frontend.objects.Collidable {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float speed;
    protected Color color;

    public GameObject(float x, float y, float width, float height, float speed, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;

    }
    public void update(float delta) {
    }

    // render Method
    public void render(ShapeRenderer shapeRenderer) {
        if (shapeRenderer != null && color != null && active) {
            shapeRenderer.setColor(this.color);
            shapeRenderer.rect(this.x, this.y, this.width, this.height);
        }
    }
    @Override
    public Rectangle getCoreHitbox() {
        return new Rectangle(x, y, width, height);}
    @Override
    public Rectangle getGrazeHitbox() {
        return new Rectangle(x - 10, y - 10, width + 20, height + 20);}
    @Override
    public void onCollision(com.Rifano.frontend.objects.Collidable other) {
    }

    public float getX(){
        return x;
    }
    public void setX(float x){
        this.x=x;
    }
    public float getY(){
        return y;
    }
    public void setY(float y){
        this.y=y;
    }
    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }
    public float getSpeed(){
        return speed;
    }
    public Color getColor(){
        return color;
    }
    public void setColor(Color color){
        this.color=color;
    }
    public void setWidth(float width) {
        if (width > 0) this.width = width;
    }

    public void setHeight(float height) {
        if (height > 0) this.height = height;
    }

    public void setSpeed(float speed) {
        if (speed >= 0) this.speed = speed;
    }

    protected boolean active = true;

    public boolean isDestroyed() {
        return active == false;
    }

    public void destroy() {
        this.active = false;
    }

    public boolean isOffScreen(float screenWidth, float screenHeight) {
        return this.x < -50 || this.x > screenWidth + 50 || this.y < -50 || this.y > screenHeight + 50;
    }

}

