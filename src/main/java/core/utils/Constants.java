package core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class Constants {
    public final static String WIN_TITLE = "OPEN-GL-003";
    public final static boolean V_SYNC = true;
    public static final float MOVE_POWER = 40f;
    public static final float MOUSE_SENSITIVITY = 0.06f;
    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static final Vector3f POINT_LIGHT_02_COL = new Vector3f(0.03f, 0.3f, 0.3f);
    public static final Vector3f POINT_LIGHT_01_COL = new Vector3f(1.0f, 1.0f, 0.8f);
    public static final Vector4f AMBIENT_LIGHT_COL = new Vector4f(0.0f, 0.0f, 0.02f, 1.0f);
    public static enum GameObjectType {NORMAL, LIGHT};
    public static final Vector3f CAMERA_INIT_POS = new Vector3f(47.037884f, 247.10669f, 299.40546f);
    public static final Vector3f CAMERA_INIT_ROT = new Vector3f(-72.219894f,84.11983f, 0.0f);

    private Constants(){}
}
