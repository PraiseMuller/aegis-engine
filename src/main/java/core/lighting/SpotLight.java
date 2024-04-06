package core.lighting;

import org.joml.Vector3f;

public class SpotLight {
    private Vector3f position;
    private Vector3f color;
    private float intensity;

    public SpotLight(Vector3f pos, Vector3f col, float ints){
        this.position = pos;
        this.color = col;
        this.intensity = ints;
    }
}
