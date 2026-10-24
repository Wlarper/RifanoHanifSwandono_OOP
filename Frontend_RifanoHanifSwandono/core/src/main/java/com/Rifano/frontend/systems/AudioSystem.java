package com.Rifano.frontend.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioSystem {
    private static AudioSystem instance;
    private Map<String, Sound> soundMap;
    private Music bgm;
    private static final float SFX_VOLUME = 1.5f;
    private static final float BGM_VOLUME = 0.5f;

    private AudioSystem() {
        soundMap = new HashMap<>();
    }
    public static AudioSystem getInstance() {
        if (instance == null) {
            instance = new AudioSystem();
        }
        return instance;
    }
    public void loadSound(String key, String filename) {
        if (soundMap.containsKey(key)) {
            return;
        }
        if (Gdx.audio != null && Gdx.files != null && Gdx.files.internal(filename).exists()) {
            soundMap.put(key, Gdx.audio.newSound(Gdx.files.internal(filename)));
        }
    }

    public void playSound(String key) {
        Sound sound = soundMap.get(key);
        if (sound != null) {
            sound.play(SFX_VOLUME);
        }
    }

    public void playBgm(String filename) {
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
            bgm = null;
        }
        if (Gdx.audio != null && Gdx.files != null && Gdx.files.internal(filename).exists()) {
            bgm = Gdx.audio.newMusic(Gdx.files.internal(filename));
            bgm.setLooping(true);
            bgm.setVolume(BGM_VOLUME);
            bgm.play();
        }
    }

    public void init() {
        loadSound("player_shoot", "Player_shoot.mp3");
        loadSound("enemy_die", "Boss_die.mp3");
        loadSound("item_get", "Item_get.mp3");
        playBgm("BattleBGM.mp3");
    }


    public void dispose() {
        for (Sound sound : soundMap.values()) {
            sound.dispose();
        }
        soundMap.clear();
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
            bgm = null;
        }
    }
}
