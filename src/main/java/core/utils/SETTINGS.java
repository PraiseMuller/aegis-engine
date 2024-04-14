package core.utils;

import core.ui.Layout;
import core.ui.VerticalLayout;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class SETTINGS {
    public final static String WIN_TITLE = "AEGIS V1.0.1. Engine";
    public static int WIN_WIDTH = 1900;
    public static int WIN_HEIGHT = 1020;
    public static boolean V_SYNC = false;
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float FOV = (float) (120);
    public static final float MOUSE_SENSITIVITY = 30f;
    public static final Layout DEFAULT_LAYOUT = new VerticalLayout();
    public static boolean BLOOM_ON = true;
    public static boolean BLACK_AND_WHITE_ON = false;
    public static boolean COLOR_INVERT = false;
    public static boolean GAMMA_CORRECT = true;
    public static boolean HDR_TONE_MAP = true;
    public static boolean WIRE_FRAME_MODE = false;
    public static final Vector3f CAMERA_INIT_POS = new Vector3f(-1.54E2f, 8.38E1f, 1.27E2f);
    public static final Vector3f CAMERA_INIT_ROT = new Vector3f(1.9E1f, 5.56E1f, 0.0E0f);
    public static final Vector3f GAME_OBJ_INIT_POSITION = new Vector3f(0.0f, 0.0f, 0.0f);
    public static float P_LIGHT_INTENSITY = 3000.0f;
    public static int NUM_P_LIGHTS = 4;
    public static float D_LIGHT_INTENSITY = 2.0f;
    public static final Vector4f BLACK = new Vector4f(0.01f, 0.01f, 0.02f, 1.0f);

    private SETTINGS(){}
}
