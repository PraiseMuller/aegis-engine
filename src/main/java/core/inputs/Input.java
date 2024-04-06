package core.inputs;

import core.engine.Scene;
import core.engine.StateMachine;
import core.engine._2D.Scene2D;
import core.renderer.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.CAMERA_INIT_POS;
import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private Input(){}

    public static void update(float dt, Scene scene){

        if(scene.getPlayer() != null) {

            float playerMovePower = scene.getPlayer().getMovePower();
            Vector3f delta = new Vector3f();
            Vector3f cameraRot = new Vector3f();

            //CAM POS
            if (KeyListener.isKeyPressed(GLFW_KEY_W))   delta.add(new Vector3f(0.0f, 0.0f, -playerMovePower * dt));
            if (KeyListener.isKeyPressed(GLFW_KEY_S))   delta.add(new Vector3f(0.0f, 0.0f, playerMovePower * dt));

            if (KeyListener.isKeyPressed(GLFW_KEY_D))   delta.add(new Vector3f(playerMovePower * dt, 0.0f, 0.0f));
            if (KeyListener.isKeyPressed(GLFW_KEY_A))   delta.add(new Vector3f(-playerMovePower * dt, 0.0f, 0.0f));

            if (KeyListener.isKeyPressed(GLFW_KEY_Q))   delta.add(new Vector3f(0.0f, playerMovePower * dt, 0.0f));
            if (KeyListener.isKeyPressed(GLFW_KEY_E))   delta.add(new Vector3f(0.0f, -playerMovePower * dt, 0.0f));

            //CAM ROT
            if (KeyListener.isKeyPressed(GLFW_KEY_LEFT))    cameraRot = new Vector3f(0.0f, -MOUSE_SENSITIVITY * 800f * dt, 0.0f);
            if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT))   cameraRot = new Vector3f(0.0f, MOUSE_SENSITIVITY * 800f * dt, 0.0f);
            if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP))     cameraRot = new Vector3f(MOUSE_SENSITIVITY * 500f * dt, 0.0f, 0.0f);
            if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN))   cameraRot = new Vector3f(-MOUSE_SENSITIVITY * 500f * dt, 0.0f, 0.0f);
            if(MouseListener.isDragging())  cameraRot = new Vector3f(-MouseListener.getDy() * MOUSE_SENSITIVITY, -MouseListener.getDx() * MOUSE_SENSITIVITY, 0.0f);

            //update player position
            if(scene.getClass().isAssignableFrom(Scene2D.class)){
                //2D
                scene.getPlayer().addPos(delta);
            }
            else {
                //3D
                scene.getCamera().movePosition(delta.x, -delta.y, delta.z);
                scene.getCamera().moveRotation(cameraRot.x, cameraRot.y, cameraRot.z);
            }
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            StateMachine.changeState();
        }


        //RELOAD CURRENT SCENE
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

    public static boolean mouseLeftClicked(){
        return MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1);
    }

    public static boolean mouseRightClicked() {
        return MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_2);
    }
    public static boolean isDragging(){ return MouseListener.isDragging(); }
    public static Vector2f getMousePosition(){
        return new Vector2f(MouseListener.getX(), MouseListener.getY());
    }

}
