package core.inputs;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class JoystickListener {

    private static JoystickListener instance = null;
    private static FloatBuffer joysticAxes;

    private JoystickListener(){}

    private JoystickListener get(){
        if(JoystickListener.instance == null){
            JoystickListener.instance = new JoystickListener();
            return JoystickListener.instance;
        }
        return JoystickListener.instance;
    }

    public FloatBuffer getJoystickAxes(){
        joysticAxes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
        return joysticAxes;
    }

}
