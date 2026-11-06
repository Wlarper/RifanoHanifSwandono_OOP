package com.Rifano.frontend.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.Rifano.frontend.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class InputHandler {

    private int keyShoot = Input.Keys.Z;
    private int keyBomb = Input.Keys.X;
    private int keyFocus = Input.Keys.SHIFT_LEFT;

    private ShootCommand shootCommand = new ShootCommand();
    private BombCommand bombCommand = new BombCommand();
    private FocusCommand focusOn = new FocusCommand(true);
    private FocusCommand focusOff = new FocusCommand(false);
    private final MoveCommand moveCommand = new MoveCommand(0, 0);

    private final List<Command> activeCommands = new ArrayList<>();

    public InputHandler() {}

    public InputHandler(List<GameObject> entities) {
        this.bombCommand.setEntities(entities);
    }
    public void setEntities(List<GameObject> entities) {
        this.bombCommand.setEntities(entities);
    }

    public int getKeyShoot() { return keyShoot; }
    public void setKeyShoot(int keyShoot) { this.keyShoot = keyShoot; }

    public int getKeyBomb() { return keyBomb; }
    public void setKeyBomb(int keyBomb) { this.keyBomb = keyBomb; }

    public int getKeyFocus() { return keyFocus; }
    public void setKeyFocus(int keyFocus) { this.keyFocus = keyFocus; }

    public List<Command> handleInput(float delta) {
        activeCommands.clear();
        if (Gdx.input == null) return activeCommands;
        // TODO:
        boolean isFocusHeld = Gdx.input.isKeyPressed(keyFocus) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        // - Check whether keyFocus or SHIFT_RIGHT is currently pressed (isKeyPressed).
        // - Store the result in a boolean variable `isFocusHeld`.
        activeCommands.add(isFocusHeld ? focusOn : focusOff);
        float dx = 0;
        float dy = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S))  dy -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A))  dx -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D))  dx += 1;
        float speedMult = isFocusHeld ? 0.45f : 1.0f;
        moveCommand.set(dx * delta * speedMult, dy * delta * speedMult);
        activeCommands.add((moveCommand));

            if (Gdx.input.isKeyPressed(keyShoot)) {
                activeCommands.add((moveCommand));
            }
            if (Gdx.input.isKeyJustPressed(keyBomb)) {
                activeCommands.add((moveCommand));
            }
    }
        return activeCommands;
    }


