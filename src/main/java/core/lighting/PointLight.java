package core.lighting;

import org.joml.Vector3f;

public class PointLight {
    private Vector3f color;
    private Vector3f position;
    private float intensity;

    public PointLight(Vector3f color, Vector3f position, float intensity){
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(PointLight p){
        this(p.color, p.position, p.intensity);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    public Attenuation getAttenuation(){
        return Attenuation.attenuationInstance;
    }
    public void multAttenuationRadius(float scalar){
        Attenuation.attenuationInstance.exponent *= scalar;
    }

    //STATIC INNER ATT CLASS
    public static class Attenuation {
        private final float constant = 0.55f;    //(intensity at closest pt?) The baby -> change this, lower = favorable. but >= 0.5f and <= 1.0f;
        private float linear = 0.06f;     //acts like intensity -> the lower, the brighter &bigger radius (better not change).
        private float exponent = 0.0001f;  //how fast intensity drops, lower value -> gradually, higher -> drastic.
        private static Attenuation attenuationInstance = Attenuation.init();

        private static Attenuation init(){
            if(Attenuation.attenuationInstance == null){
                Attenuation.attenuationInstance = new Attenuation();
            }
            return Attenuation.attenuationInstance;
        }

        private Attenuation(){}

        public float getConstant(){
            return Attenuation.attenuationInstance.constant;
        }
        public float getLinear(){
            return Attenuation.attenuationInstance.linear;
        }
        public float getExponent(){
            return Attenuation.attenuationInstance.exponent;
        }
    }
}
