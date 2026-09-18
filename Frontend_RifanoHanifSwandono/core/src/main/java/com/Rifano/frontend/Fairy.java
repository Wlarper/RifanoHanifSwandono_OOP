package com.Rifano.frontend.objects.enemies;

import com.Rifano.frontend.objects.GameObject;
import com.badlogic.gdx.graphics.Color;

public class Fairy extends Enemy {

    public Fairy(String name, int hp) {
        super(150, 380, 24, 24, Color.PINK, name, hp, 500L);
    }
    public Fairy(float x, float y, String name, int hp) {
        super(x, y, 24, 24, Color.PINK, name, hp, 500L);
    }

    @Override
    public void onCollision(Collidable other) {
        // TODO: Check whether the other received by this method is a Player
        if (other instanceof Player){
        // TODO: Print "Player touches fairy"
            System.out.println("Player Touches Fairy");}
    }

}

