package com.shchmax;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.control.*;
import com.jme3.bullet.util.*;
import com.jme3.light.PointLight;
import com.jme3.material.*;
import com.jme3.math.*;
import com.jme3.renderer.*;
import com.jme3.renderer.queue.*;
import com.jme3.scene.*;
import com.jme3.system.*;
import com.jme3.bullet.*;

import java.awt.*;

public class CrossyRoads extends SimpleApplication {
    private CustomCamera customCamera;
    private Player player;

    public static void main(String[] args) {
        CrossyRoads app = new CrossyRoads();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


        AppSettings appSettings = new AppSettings(true);
//        /* Fullscreen mode
        appSettings.setFullscreen(true);
        appSettings.setHeight(screenSize.height);
        appSettings.setWidth(screenSize.width);
//         */

        app.setSettings(appSettings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);

        customCamera = new CustomCamera(cam, flyCam, inputManager);

        player = new Player(assetManager, bulletAppState, inputManager, rootNode);

        Spatial landscapeGeometry = assetManager.loadModel("Models/Metallic Platform.glb");
        Material landscapeMaterial = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        landscapeMaterial.setFloat("Metallic", 1F);
        landscapeMaterial.setFloat("Roughness", 1F);
        landscapeGeometry.setMaterial(landscapeMaterial);
        CollisionShape landscapeCollision = CollisionShapeFactory.createMeshShape(landscapeGeometry);
        RigidBodyControl landscape = new RigidBodyControl(landscapeCollision, 0);
        landscapeGeometry.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        rootNode.attachChild(landscapeGeometry);
        rootNode.attachChild(customCamera.getCameraNode());

        PointLight sun = new PointLight();
        sun.setPosition(new Vector3f(0F, 10F, 0F));
        sun.setEnabled(true);
        sun.setRadius(40F);
        rootNode.addLight(sun);
        rootNode.setShadowMode(RenderQueue.ShadowMode.Inherit);

        initKeys();
    }

    private void initKeys() {
        //
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Camera glide
        customCamera.glideTo(player.getPhysicsLocation(), tpf);

        // Player move
        player.movePlayer(customCamera, tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //add render code here (if any)
    }
}
