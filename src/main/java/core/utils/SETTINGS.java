package core.utils;

import core.ui.Layout;
import core.ui.VerticalLayout;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class SETTINGS {
    public final static String WIN_TITLE = "AEGIS Engine V-1.0.1.";
    public static int WIN_WIDTH = 1600;
    public static int WIN_HEIGHT = 900;
    public static boolean V_SYNC = false;
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float FOV = (float) (120);
    public static final float MOUSE_SENSITIVITY = 30f;
    public static final Layout DEFAULT_LAYOUT = new VerticalLayout();
    public static boolean POST_PROCESSING = true;
    public static boolean BLACK_AND_WHITE_ON = false;
    public static boolean COLOR_INVERT = false;
    public static boolean GAMMA_CORRECT = true;
    public static boolean HDR_TONE_MAP = true;
    public static boolean WIRE_FRAME_MODE = false;
    public static final Vector3f CAMERA_INIT_POS = new Vector3f(4.95E1f, -2.00E1f, 9.98E1f);
    public static final Vector3f CAMERA_INIT_ROT = new Vector3f(-2.72E1f, 3.31E2f, 0.0E0f);
    public static final Vector3f GAME_OBJ_INIT_POSITION = new Vector3f(0.0f, 0.0f, 0.0f);
    public static float P_LIGHT_INTENSITY = 3000.0f;
    public static int NUM_P_LIGHTS = 4;
    public static float D_LIGHT_INTENSITY = 0.6f;
    public static final Vector4f BLACK = new Vector4f(0.0f,0.0f,0.0f, 1.0f);


    //anim debug
    public static int G_DISPLAY_BONE_INDEX = 0;

    private SETTINGS(){}
    //System.out.println("\n\n     "+ StandardCharsets.UTF_8.decode(byteBuffer).toString());
}