package com.Rifano.frontend;

public class Enemy {
    public String name;
    public int hp;
    public int maxHp;

    public Enemy(String name, int hp) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
    }
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
        System.out.println(this.name + " took" + damage + "damage !!! HP:" + this.hp + "/" + this.maxHp);
        if (this.hp == 0) {
            System.out.println(this.name + "was defeated YEAAHH!!!!");
        }
    }
    public void attack(Player player, int damage) {
        System.out.println(this.name+ "attacks" + player.name + " dealing "+ damage + "DMG!!!!");
        player.takeDamage(damage);

    }
    public boolean isAlive() {
        return this.hp >0;}
}

