package com.Rifano.frontend.states;

import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.patterns.entityStrategy.DiveAndBankMovement;
import com.Rifano.frontend.objects.patterns.entityStrategy.EntityMovementPattern;
import com.Rifano.frontend.objects.patterns.entityStrategy.SCurveMovement;
import com.Rifano.frontend.objects.patterns.entityStrategy.StopAndGoMovement;
import com.Rifano.frontend.objects.patterns.ShootingPattern;
import com.Rifano.frontend.objects.patterns.shootingStrategy.SpreadShot;
import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.systems.LevelWaveManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FairyWaveState implements WaveState {
    private float waveTimer = 0f;
    private Queue<SpawnEvent> timelineQueue = new LinkedList<>();

    @Override
    public void onEnter(LevelWaveManager manager) {
        buildStage1Timeline(manager.getPlayer());
    }

    @Override
    public void update(LevelWaveManager manager, float delta) {
        waveTimer += delta;
        while (!timelineQueue.isEmpty() && timelineQueue.peek().getTriggerTime() < waveTimer) {
            SpawnEvent event = timelineQueue.poll();
            event.execute(manager);
        }
        updateActiveFairies(manager, delta);
        if ((timelineQueue.isEmpty() && manager.areAllFairiesDefeated()) || waveTimer > 33.5f) {
            manager.setState(new BossPhase1State());

        }
    }

    // ====================================================
    // Helper / Timeline Data
    // ====================================================
    private void buildStage1Timeline(Player player) {
        // --- Wave 1: Left S-Curve Stream (Sub0, 5 Blue Popcorn Fairies, ticks 128 - 192) ---
        float[] w1X = {60f, 68f, 76f, 84f, 92f};
        for (int i = 0; i < w1X.length; i++) {
            float time = 2.13f + i * 0.267f;
            timelineQueue.add(new SpawnEvent(time, w1X[i], 480f, "Left S-Curve Fairy", 8,
                new SCurveMovement(130f, (float) Math.toRadians(270), false),
                null));
        }

        // --- Wave 2: Right Mirrored S-Curve Stream (Sub0, 8 Blue Popcorn Fairies, ticks 256 - 368) ---
        float[] w2X = {324f, 316f, 308f, 300f, 292f, 284f, 276f, 268f};
        for (int i = 0; i < w2X.length; i++) {
            float time = 4.27f + i * 0.267f;
            timelineQueue.add(new SpawnEvent(time, w2X[i], 480f, "Right S-Curve Fairy", 8,
                new SCurveMovement(130f, (float) Math.toRadians(270), true),
                null));
        }

        // --- Wave 3: Dual Peeling Streams (Sub1, 10 pairs of Blue Popcorn Fairies, ticks 432 - 576) ---
        for (int i = 0; i < 10; i++) {
            float time = 7.20f + i * 0.267f;
            float leftX = 32f + i * 16f;
            float rightX = 352f - i * 16f;

            timelineQueue.add(new SpawnEvent(time, leftX, 480f, "Wave 3 Left Fairy", 8,
                new DiveAndBankMovement(130f, (float) Math.toRadians(270), 1.67f, -1.1781f, 1.67f),
                null));

            timelineQueue.add(new SpawnEvent(time, rightX, 480f, "Wave 3 Right Fairy", 8,
                new DiveAndBankMovement(130f, (float) Math.toRadians(270), 1.67f, 1.1781f, 1.67f),
                null));
        }

        // --- Wave 4: Aimed Red Fairies (Sub2 / Sub3, 16 fairies, ticks 640 - 1110) ---
        float[][] snipers = {
            {10.67f, 32f, 0f, 1f},   // [0]=time, [1]=x, [2]=exitRight (0 or 1), [3]=isSub2 (1 = Sub2 shooter, 0 = Sub3 non-shooter)
            {11.67f, 256f, 1f, 0f},
            {12.50f, 128f, 0f, 0f},
            {13.33f, 352f, 1f, 1f},
            {14.17f, 24f, 0f, 0f},
            {14.83f, 304f, 1f, 0f},
            {15.50f, 144f, 0f, 1f},
            {16.00f, 344f, 1f, 0f},
            {16.50f, 32f, 0f, 0f},
            {17.00f, 240f, 1f, 1f},
            {17.33f, 24f, 0f, 0f},
            {17.67f, 304f, 1f, 0f},
            {18.00f, 144f, 0f, 1f},
            {18.17f, 344f, 1f, 0f},
            {18.33f, 32f, 0f, 1f},
            {18.50f, 240f, 1f, 1f}
        };

        for (float[] s : snipers) {
            float time = s[0];
            float x = s[1];
            boolean exitRight = (s[2] == 1f);
            boolean isSub2Shooter = (s[3] == 1f);

            ShootingPattern shootPat = isSub2Shooter
                ? new SpreadShot(160f, 5, 144f, 10, player, null)
                : null;

            timelineQueue.add(new SpawnEvent(time, x, 480f, isSub2Shooter ? "Red Sniper Fairy" : "Red Hover Fairy", 32,
                new StopAndGoMovement(130f, (float) Math.toRadians(270), 1.0f, 1.17f, exitRight, 150f),
                shootPat, isSub2Shooter ? ItemType.POWER : null, "RED"));
        }

        // --- Wave 5: High-Speed Popcorn Ambush (ticks 1220 - 1400) ---
        boolean[] isSub1 = {
            false, false, true, false, true, false, false, true, true, false,
            false, false, false, true, true, false, false, true, true
        };
        boolean[] isMirrored = {
            true, false, true, false, true, false, true, false, true, false,
            false, true, false, true, false, true, false, true, false
        };
        float[] popcornX = {
            240f, 65f, 315f, 135f, 275f, 90f, 340f, 175f, 50f, 220f,
            300f, 110f, 260f, 80f, 330f, 190f, 70f, 285f, 150f
        };

        for (int i = 0; i < popcornX.length; i++) {
            float time = 20.33f + i * 0.167f;
            EntityMovementPattern mov = isSub1[i]
                ? new DiveAndBankMovement(140f, (float) Math.toRadians(270), 1.67f, isMirrored[i] ? 1.1781f : -1.1781f, 1.67f)
                : new SCurveMovement(140f, (float) Math.toRadians(270), isMirrored[i]);

            timelineQueue.add(new SpawnEvent(time, popcornX[i], 480f, "Popcorn Ambush Fairy", 8, mov, null));
        }

        // --- Wave 6: Pre-Midboss Dual Flank Streams (ticks 1600 - 1808) ---
        for (int i = 0; i < 6; i++) {
            float time = 26.67f + i * 0.267f;
            float x1 = 320f - i * 8f;
            float x2 = 352f - i * 8f;
            timelineQueue.add(new SpawnEvent(time, x1, 480f, "Pre-Midboss Right Column", 3,
                new SCurveMovement(140f, (float) Math.toRadians(270), true), null));
            timelineQueue.add(new SpawnEvent(time, x2, 480f, "Pre-Midboss Right Column", 3,
                new SCurveMovement(140f, (float) Math.toRadians(270), true), null));
        }

        for (int i = 0; i < 8; i++) {
            float time = 28.27f + i * 0.267f;
            float x1 = 64f + i * 8f;
            float x2 = 96f + i * 8f;
            timelineQueue.add(new SpawnEvent(time, x1, 480f, "Pre-Midboss Left Column", 3,
                new SCurveMovement(140f, (float) Math.toRadians(270), false), null));
            timelineQueue.add(new SpawnEvent(time, x2, 480f, "Pre-Midboss Left Column", 3,
                new SCurveMovement(140f, (float) Math.toRadians(270), false), null));
        }
    }

    private void updateActiveFairies(LevelWaveManager manager, float delta) {
        List<Fairy> fairies = manager.getFairies();
        if (fairies != null) {
            var fIter = fairies.iterator();
            while (fIter.hasNext()) {
                Fairy fairy = fIter.next();
                if (fairy == null || fairy.isDestroyed() || fairy.isOffScreen(416, 464)) {
                    if (fairy != null && !fairy.isDestroyed()) {
                        fairy.destroy();
                    }
                    fIter.remove();
                    continue;
                }

                boolean insidePlayfield = fairy.getX() >= 20 && fairy.getX() <= 420
                    && fairy.getY() >= 0 && fairy.getY() <= 464;

                if (fairy.getMovementPattern() instanceof StopAndGoMovement) {
                    StopAndGoMovement stopAndGo = (StopAndGoMovement) fairy.getMovementPattern();
                    if (stopAndGo.shouldFireSpread() && insidePlayfield && fairy.getShootingPattern() != null) {
                        fairy.shootBullet(manager.getBulletManager());
                    }
                } else {
                    if (fairy.getShootingPattern() != null && fairy.canShoot(delta) && insidePlayfield) {
                        fairy.shootBullet(manager.getBulletManager());
                    }
                }
            }
        }
    }
}
