package com.Rifano.frontend.commands;

import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.systems.BulletManager;

public class ShootCommand implements Command {
    @Override
    public void execute(Player player, BulletManager bulletManager) {
        if (player != null) {
            player.shootBullet(bulletManager);
        }
    }
}
