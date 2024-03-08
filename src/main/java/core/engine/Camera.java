package core.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.Z_FAR;

public class Camera {
    private static Matrix4f projection = null;
    private static Matrix4f view = null;
    private static Vector3f position = new Vector3f();

    private Camera(){}

    public static void init(){
        Camera.projection = new Matrix4f();
        Camera.projection.identity();
        Camera.projection.ortho(0f, WIN_WIDTH, WIN_HEIGHT, 0, Z_NEAR, Z_FAR, false);

        Camera.update(new Vector3f());
    }

    public static void update(Vector3f npos){
        Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);
        Camera.position.add(npos);

        Camera.view = new Matrix4f();
        Camera.view.identity();
        Camera.view.lookAt(new Vector3f(position.x, position.y, 20), camFront.add(position.x, position.y, 0), camUp);
    }

    public static Matrix4f projectionMatrix(){
        return Camera.projection;
    }

    public static Matrix4f viewMatrix(){
        return Camera.view;
    }
}
