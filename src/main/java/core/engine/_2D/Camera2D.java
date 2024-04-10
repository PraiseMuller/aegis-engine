package core.engine._2D;

import core.engine.Camera;
import core.entities.GameObject;
import core.renderer.Window;
import core.utils.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.Z_FAR;

public class Camera2D extends Camera {

    public Camera2D(){
        super();
        this.projection.identity();
        this.projection.ortho(0f, WIN_WIDTH, WIN_HEIGHT, 0, Z_NEAR, Z_FAR, false);
    }

    @Override
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        this.smoothFollow(new Vector3f(offsetX, offsetY, offsetZ));
    }

    @Override
    public void smoothFollow(Vector3f c) {
        float smoothing = 0.01f;
        Vector3f desiredPosition = new Vector3f(c.x - WIN_WIDTH / 2f, c.y - WIN_HEIGHT / 2f, c.z);

        this.position.x = MathUtils.lerp(position.x, desiredPosition.x, smoothing);
        this.position.y = MathUtils.lerp(position.y, desiredPosition.y, smoothing);
        this.position.z = MathUtils.lerp(position.z, desiredPosition.z, smoothing);
    }

    @Override
    public Matrix4f viewMatrix(){
        this.view.identity();

        // First do the rotation so camera rotates over its position
        view.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

        // Then do the translation
        view.translate(-position.x, -position.y, -position.z);
        return view;
    }

    @Override
    public Matrix4f modelMatrix(GameObject obj){
        this.model.identity();
        this.model.identity().translate(obj.getPosition())
                .rotateX((float)Math.toRadians(-obj.getRotation().x))
                .rotateY((float)Math.toRadians(-obj.getRotation().y))
                .rotateZ((float)Math.toRadians(-obj.getRotation().z))
                .scale(obj.getScale());

        return this.model;  // this.view.mul(this.model);
    }
}
