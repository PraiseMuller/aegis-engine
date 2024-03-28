package core.engine;

import core.utils.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.Z_FAR;

public class Camera {
    private static Matrix4f projection = null;
    private static Matrix4f view = null;
    private static final Vector3f position = new Vector3f(0.0f, 0.0f, 20.0f);

    private Camera(){}

    public static void init(){
        Camera.view = new Matrix4f();
        Camera.projection = new Matrix4f();

        Camera.projection.identity();
        Camera.projection.ortho(0f, WIN_WIDTH, WIN_HEIGHT, 0, Z_NEAR, Z_FAR, false);

        Camera.updatePos(Camera.position);
    }

    public static void updatePos(Vector3f npos){
        Vector3f camFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);

        Camera.position.add(npos.x, npos.y, 0);
        Camera.view.identity();
        Camera.view.lookAt(new Vector3f(position.x, position.y, 30), camFront.add(position.x, position.y, 0), camUp);
    }

    public static void smoothFollow(Vector3f c) {
        float smoothing = 0.01f;
        Vector3f desiredPosition = new Vector3f(c.x - Window.getWidth() / 2f, c.y - Window.getHeight() / 2f, c.z);

        Camera.position.x = MathUtils.lerp(position.x, desiredPosition.x, smoothing);
        Camera.position.y = MathUtils.lerp(position.y, desiredPosition.y, smoothing);
        Camera.position.z = MathUtils.lerp(position.z, desiredPosition.z, smoothing);

        Vector3f camFront = new Vector3f(position.x, position.y, -1);
        Vector3f camUp = new Vector3f(0.0f, 1.0f, 0.0f);

        Camera.view.identity();
        Camera.view.lookAt(position, camFront, camUp);
    }

    public static Matrix4f projectionMatrix(){
        return Camera.projection;
    }

    public static Matrix4f viewMatrix(){
        return Camera.view;
    }

    public static Vector3f getPosition(){
        return Camera.position;
    }
}
