package com.Rifano.frontend.objects.enemies;

import com.Rifano.frontend.objects.GameObject; 
import com.Rifano.frontend.objects.Player;
import com.badlogic.gdx.graphics.Color;

public class Enemy extends GameObject {
    protected String name;
    protected int hp;
    protected int maxHp;
    protected long scoreValue;

    public Enemy(String name, int hp) {
        super(200, 380, 24, 24, 0, Color.PINK);
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.scoreValue = 100;
    }

    public Enemy(float x, float y, float width, float height, Color color, String name, int hp, long scoreValue) {
        super(x, y, width, height, 0, color);
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.scoreValue = scoreValue;
    }

    public boolean takeDamage(int damage) {
        if (this.hp <= 0) {
            return false;
        }
        setHp(getHp() - damage);

        System.out.println(this.name + " took " + damage + " damage !!! HP: " + this.hp + "/" + this.maxHp);

        if (this.hp == 0) {
            System.out.println(this.name + " was defeated YEAAHH!!!!");
            return true;
        }
        return false;
    }

    public void attack(Player player, int damage) {
        System.out.println(this.name + "attacks" + player.getName() + " dealing " + damage + "DMG!!!!");
        player.takeDamage(damage);

    }

    public boolean isAlive() {
        return this.hp > 0;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }
    public int getMaxHp() {
        return maxHp;
    }
    public long getScoreValue() {
        return scoreValue;
    }
    public void setScoreValue(long scoreValue) {
        this.scoreValue = scoreValue;
    }
}
