package core.inputs;

import core.engine.Camera;
import core.engine.Scene;
import core.engine.StateMachine;
import core.engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final float MOVE_POWER = 200.0f;
    private Input(){}

    public static void update(float dt, Scene scene){

        Vector3f camDelta = new Vector3f();
        if(KeyListener.isKeyPressed(GLFW_KEY_W)){
            camDelta.add(new Vector3f(0.0f, -MOVE_POWER  * dt, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)){
            camDelta.add(new Vector3f(0.0f, MOVE_POWER * dt, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)){
            camDelta.add(new Vector3f(MOVE_POWER * dt, 0.0f, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)){
            camDelta.add(new Vector3f(-MOVE_POWER * dt, 0.0f, 0.0f));
        }

        Camera.update(camDelta);

        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            StateMachine.changeState();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_R)){
            scene.loadParticles();
        }
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
