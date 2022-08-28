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

    public Vector3f getPlaneDirectionVector() {
        Vector3f directionVector = camera.getDirection();
        Vector3f verticalVector = chaseCamera.getUpVector();
        return directionVector.subtract(verticalVector.mult(verticalVector.dot(directionVector))).normalize();
    }

    public void shiftCamera(float right, float up) {
        Vector3f verticalVector = chaseCamera.getUpVector();
        Vector3f planeForwardVector = getPlaneDirectionVector();
        Vector3f planeRightVector = planeForwardVector.cross(verticalVector).normalize();
        Vector3f movement = planeRightVector.mult(right).add(planeForwardVector.mult(up)).mult(mouseShiftSpeed).mult(chaseCamera.getDistanceToTarget());
        cameraNode.move(movement);
    }

    public Node getCameraNode() {
        return cameraNode;
    }

    public void resetPosition() {
        glideBegin = cameraNode.getLocalTranslation().clone();
    }

    public void glideTo(Node place, float tpf) {
        Vector3f glideEnd = place.getLocalTranslation();
        Vector3f glide = cameraNode.getLocalTranslation();
        Vector3f toEnd = glideEnd.subtract(glide);
        Vector3f fromBegin = glide.subtract(glideBegin);
        if (toEnd.dot(fromBegin) < 0 && toEnd.length() <= dGlide) {
            Vector3f movement = toEnd.normalize().mult(cameraGlideSpeed * tpf);
            if (movement.length() >= toEnd.length()) {
                movement = toEnd;
                glideBegin = null;
            }
            cameraNode.move(movement);
        } else {
            if (toEnd.dot(fromBegin) < 0) {
                fromBegin = toEnd;
            }
            cameraNode.move(toEnd.normalize().mult((Math.min(toEnd.length(), fromBegin.length()) + dGlide) * cameraGlideSpeed * tpf));
        }
    }

    public boolean isGlide() {
        return glideBegin != null;
    }
}
