package core.inputs;

import core.engine.Scene;
import core.engine.StateMachine;
import core.renderer.Window;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.CAMERA_INIT_POS;
import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final Vector3f delta = new Vector3f();
    private static final Vector3f cameraRot = new Vector3f();
    private Input(){}

    public static void update(float dt, Scene scene){

        delta.zero();
        cameraRot.zero();

        //CAM POS
        float cameraMoveSpeed = MOUSE_SENSITIVITY;
        if (KeyListener.isKeyPressed(GLFW_KEY_W))   delta.z = -cameraMoveSpeed * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_S))   delta.z =  cameraMoveSpeed * dt;

        if (KeyListener.isKeyPressed(GLFW_KEY_D))   delta.x =  cameraMoveSpeed * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_A))   delta.x = -cameraMoveSpeed * dt;

        if (KeyListener.isKeyPressed(GLFW_KEY_Q))   delta.y =  cameraMoveSpeed * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_E))   delta.y = -cameraMoveSpeed * dt;

        //CAM ROT
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))    cameraRot.y = -MOUSE_SENSITIVITY * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))   cameraRot.y = MOUSE_SENSITIVITY * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP)) cameraRot.x = MOUSE_SENSITIVITY * dt;
        if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN))   cameraRot.x = -MOUSE_SENSITIVITY  * dt;

//        if(MouseListener.isDragging()) {
//            cameraRot.x = -MouseListener.getDy() * MOUSE_SENSITIVITY / 1000.0f;
//            cameraRot.y = -MouseListener.getDx() * MOUSE_SENSITIVITY / 1000.0f;
//            cameraRot.z = 0.0f;
//        }

        //MOVE CAMERA
        scene.getCamera().movePosition(delta.x, -delta.y, delta.z);
        scene.getCamera().moveRotation(cameraRot.x, cameraRot.y, cameraRot.z);


        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            scene.stateMachine().changeState();
            G_DISPLAY_BONE_INDEX++;
            if(G_DISPLAY_BONE_INDEX > 4) G_DISPLAY_BONE_INDEX = 0;
        }


        //RELOAD CURRENT CAMERA
        if(KeyListener.isKeyPressed(GLFW_KEY_R)){
            scene.camera.getPosition().x = CAMERA_INIT_POS.x;
            scene.camera.getPosition().y = CAMERA_INIT_POS.y;
            scene.camera.getPosition().z = CAMERA_INIT_POS.z;

            scene.camera.getRotation().x = CAMERA_INIT_ROT.x;
            scene.camera.getRotation().y = CAMERA_INIT_ROT.y;
            scene.camera.getRotation().z = CAMERA_INIT_ROT.z;
        }


        MouseListener.endFrame();
    }
}
