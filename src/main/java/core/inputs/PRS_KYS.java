package core.inputs;

import core.Window;
import core.render.Renderer;
import core.utils.Constants;
import org.joml.Vector3f;

import static core.utils.Constants.MOUSE_SENSITIVITY;
import static core.utils.Constants.MOVE_POWER;
import static org.lwjgl.glfw.GLFW.*;

public class PRS_KYS {
    private static Vector3f cameraInc;

    private PRS_KYS(){}

    public static void update(float dt, Renderer renderer){
        if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
            glfwSetWindowShouldClose(Window.getWindow(), true);
        }

        //move camera
        cameraInc = new Vector3f();

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) cameraInc.z = -dt * MOVE_POWER;
        else if (KeyListener.isKeyPressed(GLFW_KEY_S)) cameraInc.z = dt * MOVE_POWER;

        if (KeyListener.isKeyPressed(GLFW_KEY_A)) cameraInc.x = -dt * MOVE_POWER;
        else if (KeyListener.isKeyPressed(GLFW_KEY_D)) cameraInc.x = dt * MOVE_POWER;

        if (KeyListener.isKeyPressed(GLFW_KEY_Z)) cameraInc.y = -dt * MOVE_POWER;
        else if (KeyListener.isKeyPressed(GLFW_KEY_X)) cameraInc.y = dt * MOVE_POWER;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) renderer.camera.moveRotation(0, -MOVE_POWER * MOUSE_SENSITIVITY, 0.0f);
        else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) renderer.camera.moveRotation(0, MOVE_POWER * MOUSE_SENSITIVITY, 0);

        if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            renderer.camera.setPosition(new Vector3f(47.037884f, 247.10669f, 299.40546f));
            renderer.camera.setRotation(new Vector3f(-72.219894f,84.11983f, 0.0f));
        }

        //update camera pos
        renderer.camera.movePosition(cameraInc.x, cameraInc.y, cameraInc.z);
//        System.out.println("x: "+renderer.camera.getPosition().x + "\ty: "+renderer.camera.getPosition().y +"\tz: "+renderer.camera.getPosition().z );
//        System.out.println("x: "+renderer.camera.getRotation().x + "\ty: "+renderer.camera.getRotation().y +"\tz: "+renderer.camera.getRotation().z +"\n");

        //rotations
        if(MouseListener.isDragging())
            renderer.camera.moveRotation(-MouseListener.getDy() * MOUSE_SENSITIVITY, -MouseListener.getDx() * MOUSE_SENSITIVITY, 0.0f);
//        MouseListener.endFrame();
    }

}
