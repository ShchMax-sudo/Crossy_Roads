package com.shchmax;

import com.jme3.app.SimpleApplication;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.material.*;
import com.jme3.math.*;
import com.jme3.renderer.*;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;
import com.jme3.scene.shape.Line;
import com.jme3.system.*;

import java.awt.*;

public class CrossyRoads extends SimpleApplication {
    private CustomCamera customCamera;
    private Node player;

    public static void main(String[] args) {
        CrossyRoads app = new CrossyRoads();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setFullscreen(true);
        appSettings.setHeight(screenSize.height);
        appSettings.setWidth(screenSize.width);

        app.setSettings(appSettings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box box = new Box(0.5F, 0.5F, 0.5F);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setLocalTranslation(new Vector3f(0F, 0.5F, 0F));
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Blue);
        boxGeometry.setMaterial(material);

        player = new Node("Player");
        player.attachChild(boxGeometry);

        customCamera = new CustomCamera(cam, flyCam, inputManager);

        int cnt = 5;
        float lineSize = 1;
        Node floorGrid = new Node("Floor Grid");

        for (int i = -cnt; i <= cnt; ++i) {
            Line horLine = new Line(new Vector3f(-cnt * lineSize, 0, i * lineSize), new Vector3f(cnt * lineSize, 0, i * lineSize));
            Geometry horLineGeometry = new Geometry("Line " + i, horLine);
            Line verLine = new Line(new Vector3f(i * lineSize, 0, -cnt * lineSize), new Vector3f(i * lineSize, 0, cnt * lineSize));
            Geometry verLineGeometry = new Geometry("Line " + i, verLine);
            Material lineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            lineMaterial.setColor("Color", ColorRGBA.Magenta);
            horLineGeometry.setMaterial(lineMaterial);
            verLineGeometry.setMaterial(lineMaterial);
            floorGrid.attachChild(horLineGeometry);
            floorGrid.attachChild(verLineGeometry);
        }

        rootNode.attachChild(player);
        rootNode.attachChild(customCamera.getCameraNode());
        rootNode.attachChild(floorGrid);

        initKeys();
    }

    private void initKeys() {
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (name.equals("Space") && isPressed) {
                customCamera.resetPosition();
            }
        }, "Space");
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Camera glide

        if (customCamera.isGlide()) {
            customCamera.glideTo(player, tpf);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //add render code here (if any)
    }
}
