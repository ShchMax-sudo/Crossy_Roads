package com.shchmax;

import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.Vector3f;
import com.jme3.renderer.*;
import com.jme3.scene.*;

public class CustomCamera {
    private final Camera camera;
    private final ChaseCamera chaseCamera;
    private final Node cameraNode;
    private final InputManager inputManager;
    private Vector3f glideBegin;
    private boolean mouseShiftMode = false;
    private boolean mouseRotateMode = false;
    private boolean cursorVisible = true;
    private boolean synchrony = true;
    private boolean gliding = false;
    private final float mouseShiftSpeed = 7.1F;
    private final float cameraGlideSpeed = 3F;
    private final float dGlide = 0.1F;

    public CustomCamera(Camera camera, FlyByCamera flyCamera, InputManager inputManager) {
        this.camera = camera;
        this.inputManager = inputManager;
        cameraNode = new Node("Camera Node");
        flyCamera.setEnabled(false);
        chaseCamera = new ChaseCamera(camera, cameraNode, inputManager);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        chaseCamera.setInvertVerticalAxis(true);
        chaseCamera.setChasingSensitivity(0);
        chaseCamera.setTrailingRotationInertia(0);
        chaseCamera.setTrailingEnabled(false);
        chaseCamera.setMinDistance(2.0F);
        chaseCamera.setMaxDistance(40.0F);
        chaseCamera.setDownRotateOnCloseViewOnly(false);
        initKeys(inputManager);
    }

    private void initKeys(InputManager inputManager) {
        inputManager.addMapping("Mouse Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Mouse Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("Mouse Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("Mouse Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Mouse LMB", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Mouse MMB", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("Mouse RMB", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        inputManager.addListener((com.jme3.input.controls.ActionListener) (name, isPressed, tpf) -> {
            if (name.equals("Mouse MMB")) {
                mouseShiftMode = isPressed;
            }
            if (name.equals("Mouse RMB")) {
                mouseRotateMode = isPressed;
                mouseShiftMode &= !isPressed;
            }
            updateCursor();
        }, "Mouse RMB", "Mouse MMB");
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            float moveUp = 0;
            float moveRight = 0;
            if (mouseShiftMode && !mouseRotateMode) {
                if (name.equals("Mouse Left")) {
                    moveRight += value * tpf;
                }
                if (name.equals("Mouse Right")) {
                    moveRight -= value * tpf;
                }
                if (name.equals("Mouse Up")) {
                    moveUp -= value * tpf;
                }
                if (name.equals("Mouse Down")) {
                    moveUp += value * tpf;
                }
                moveUp *= mouseShiftSpeed;
                moveRight *= mouseShiftSpeed;
                synchrony = false;
                shiftCamera(moveRight, moveUp);
            }
        }, "Mouse Up", "Mouse Down", "Mouse Right", "Mouse Left");
    }

    public void updateCursor() {
        if (mouseShiftMode && cursorVisible) {
            inputManager.setCursorVisible(false);
            cursorVisible = false;
        } else if (!mouseShiftMode && !cursorVisible) {
            inputManager.setCursorVisible(true);
            cursorVisible = true;
        }
    }

    public Vector3f getUpDirectionVector() {
        Vector3f directionVector = camera.getDirection();
        Vector3f verticalVector = chaseCamera.getUpVector();
        return directionVector.subtract(verticalVector.mult(verticalVector.dot(directionVector))).normalize();
    }

    public Vector3f getRightDirectionVector() {
        Vector3f verticalVector = chaseCamera.getUpVector();
        return getUpDirectionVector().cross(verticalVector).normalize();
    }

    public void shiftCamera(float right, float up) {
        cameraNode.move(getRightDirectionVector().mult(right).add(getUpDirectionVector().mult(up)).mult(mouseShiftSpeed).mult(chaseCamera.getDistanceToTarget()));
    }

    public Node getCameraNode() {
        return cameraNode;
    }

    public void sync() {
        if (!synchrony) {
            glideBegin = cameraNode.getLocalTranslation().clone();
        }
        gliding = true;
        synchrony = true;
    }

    public void desync() {
        if (!gliding) {
            synchrony = false;
        }
    }

    public void glideTo(Vector3f glideEnd, float tpf) {
        if (!synchrony) {
            return;
        }
        Vector3f glide = cameraNode.getLocalTranslation();
        Vector3f toEnd = glideEnd.subtract(glide);
        Vector3f fromBegin;
        if (glideBegin != null) {
            fromBegin = glide.subtract(glideBegin);
        } else {
            fromBegin = toEnd;
        }
        if (fromBegin.length() > toEnd.length()) {
            glideBegin = null;
        }
        if (toEnd.dot(fromBegin) < 0 && toEnd.length() <= dGlide) {
            Vector3f movement = toEnd.normalize().mult(cameraGlideSpeed * tpf);
            if (movement.length() >= toEnd.length()) {
                movement = toEnd;
                glideBegin = null;
                synchrony = true;
                gliding = false;
            }
            cameraNode.move(movement);
        } else {
            cameraNode.move(toEnd.normalize().mult((Math.min(toEnd.length(), fromBegin.length()) + dGlide) * cameraGlideSpeed * tpf));
        }
    }
}
