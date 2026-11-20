package com.Rifano.frontend.states;

import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.systems.LevelWaveManager;

public class StageClearState implements WaveState {
    @Override
    public void onEnter(LevelWaveManager manager) {
        System.out.println("=== STAGE 1 CLEAR! ===");
        Boss boss = manager.getBoss();
        if (boss != null && !boss.isDestroyed()) {
            boss.destroy();
        }
    }

    @Override
    public void update(LevelWaveManager manager, float delta) {
    }
}
