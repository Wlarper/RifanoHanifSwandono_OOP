package com.Rifano.frontend.states;

import com.Rifano.frontend.systems.LevelWaveManager;

public interface WaveState {
    void onEnter(LevelWaveManager manager);
    void update(LevelWaveManager manager, float delta);
}
