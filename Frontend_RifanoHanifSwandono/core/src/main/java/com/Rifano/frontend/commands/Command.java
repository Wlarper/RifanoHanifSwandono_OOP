
package com.Rifano.frontend.commands;

import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.systems.BulletManager;

public interface Command {
    void execute(Player player, BulletManager bulletManager);
}
