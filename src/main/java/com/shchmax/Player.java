package com.shchmax;

import com.jme3.asset.*;
import com.jme3.bullet.*;
import com.jme3.bullet.control.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.*;
import com.jme3.scene.*;

public class Player {
    private final Node playerNode;
    private final BetterCharacterControl playerControl;
    private final Vector3f walkDirection = Vector3f.ZERO;
    private boolean up = false;
    private boolean down = false;
    private boolean right = false;
    private boolean left = false;
    private boolean isWalking = false;
    private final float speed = 3F;
    private final float rotationSpeed = 5F;

    public Player(AssetManager assetManager, BulletAppState bulletAppState, InputManager inputManager, Node rootNode) {
        Spatial geometry = assetManager.loadModel("Models/Player.glb");
        geometry.setLocalTranslation(new Vector3f(0F, 0.5F, 0F));
        playerNode = new Node();
        playerControl = new BetterCharacterControl(0.5F, 1F, 1F);
        playerNode.attachChild(geometry);
        playerNode.addControl(playerControl);
        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().addAll(playerNode);
        rootNode.attachChild(playerNode);
        initKeys(inputManager);
    }

    private void initKeys(InputManager inputManager) {
        inputManager.addMapping("W",  new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("A",  new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("S",  new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("D",  new KeyTrigger(KeyInput.KEY_D));

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            switch (name) {
                case "W":
                    up = isPressed;
                    break;
                case "S":
                    down = isPressed;
                    break;
                case "A":
                    left = isPressed;
                    break;
                case "D":
                    right = isPressed;
                    break;
            }
        }, "W", "A", "S", "D");
    }

    public void movePlayer(CustomCamera camera, float tpf) {
        Vector3f cameraUp = camera.getUpDirectionVector();
        Vector3f cameraRight = camera.getRightDirectionVector();
        cameraRight = cameraRight.add(cameraUp.mult(0.01F)).normalize();
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(cameraRight.negate());
        }
        if (right) {
            walkDirection.addLocal(cameraRight);
        }
        if (up) {
            walkDirection.addLocal(cameraUp);
        }
        if (down) {
            walkDirection.addLocal(cameraUp.negate());
        }
        if (!walkDirection.equals(new Vector3f(0, 0, 0)) || isWalking) {
            camera.sync();
        } else {
            camera.desync();
        }
        isWalking = !walkDirection.equals(new Vector3f(0, 0, 0));
        playerControl.setWalkDirection(walkDirection.normalize().mult(speed));
        if (isWalking) {
            rotatePlayer(walkDirection, tpf);
        }
    }

    public void rotatePlayer(Vector3f up, float tpf) {
        Vector3f down = up.negate();
        Vector3f right = new Quaternion().fromAngleAxis((float) (Math.PI / 2), Vector3f.UNIT_Y).toRotationMatrix().mult(up);
        Vector3f left = right.negate();
        Vector3f facing = playerControl.getViewDirection();
        float uc = up.dot(facing);
        float dc = down.dot(facing);
        float rc = right.dot(facing);
        float lc = left.dot(facing);
        float bestC = Math.max(Math.max(uc, dc), Math.max(lc, rc));
        Vector3f best;
        if (bestC == uc) {
            best = up;
        } else if (bestC == dc) {
            best = down;
        } else if (bestC == lc) {
            best = left;
        } else {
            best = right;
        }
        float angle = (float)(Math.atan2(facing.cross(best).dot(Vector3f.UNIT_Y), facing.dot(best)));
        playerControl.setViewDirection(new Quaternion().fromAngleAxis(angle * tpf * rotationSpeed, Vector3f.UNIT_Y).toRotationMatrix().mult(playerControl.getViewDirection()));
        playerControl.update(tpf);
    }

    public Vector3f getPhysicsLocation() {
        return playerControl.getRigidBody().getPhysicsLocation();
    }
}
