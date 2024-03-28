package core.inputs;

import core.engine.Camera;
import core.engine.Scene;
import core.engine.StateMachine;
import core.engine.Window;
import core.entities.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Input {
    private Input(){}

    public static void update(float dt, Scene scene){

        float playerMovePower = scene.getPlayer().getMovePower();
        Vector3f delta = new Vector3f();

        if(KeyListener.isKeyPressed(GLFW_KEY_W)){
            delta.add(new Vector3f(0.0f, -playerMovePower  * dt, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)){
            delta.add(new Vector3f(0.0f, playerMovePower * dt, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)){
            delta.add(new Vector3f(playerMovePower * dt, 0.0f, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)){
            delta.add(new Vector3f(-playerMovePower * dt, 0.0f, 0.0f));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_Q)){
            delta.add(new Vector3f(0.0f, 0.0f, playerMovePower * dt));
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_E)){
            delta.add(new Vector3f(0.0f, 0.0f, -playerMovePower * dt));
        }


        //mouse clicked.. move player
        if(MouseListener.mouseButtonDown(0) || MouseListener.isDragging()){
            scene.getPlayer().follow(MouseListener.getX(), MouseListener.getY());
            MouseListener.endFrame();
        }

        //update player pos#
        scene.getPlayer().addPos(delta);

        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            StateMachine.changeState();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_R)){
            scene.loadParticles();
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
