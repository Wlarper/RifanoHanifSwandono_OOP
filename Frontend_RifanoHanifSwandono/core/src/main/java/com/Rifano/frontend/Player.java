package com.Rifano.frontend;

import com.badlogic.gdx.graphics.Color;

public class Player extends GameObject {
    private String name;
    private int hp;
    private int power;
    private int spellCards;
    private long score;

    public Player(String name, int hp, int power, int spellCards) {
        super(280, 40, 32, 32, 0, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public Player(float x, float y, String name, int hp, int power, int spellCards) {
        super(x, y, 32, 32, 0, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public void takeDamage(int damage) {
        setHp(getHp() - damage);

        if (this.hp > 0) {
            System.out.println(this.name + " took " + damage + " damage!!!  Remaining HP:" + this.hp);
        } else {
            System.out.println(this.name + " DIEEDDDD BOOOOO ");
        }
    }

    public void shoot(Enemy target) {
        int damage = 10 + getPower();
        System.out.println(getName() + " shoots " + target.getName() + " dealing " + damage + " DMG!");
        target.takeDamage(damage);
    }

    public void addScore(long points) {
        if (points > 0) {
            this.score += points;
        }
    }

    public boolean isAlive() {
        return this.hp > 0;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public int getSpellCards() {
        return spellCards;
    }

    public void setSpellCards(int spellCards) {
        this.spellCards = spellCards;
    }

    public long getScore() {
        return score;
    }
}

