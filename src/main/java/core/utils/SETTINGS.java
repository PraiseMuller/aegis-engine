package core.utils;

import core.ui.Layout;
import core.ui.VerticalLayout;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class SETTINGS {
    public final static String WIN_TITLE = "AEGIS Engine";
    public static int WIN_WIDTH = 1900;
    public static int WIN_HEIGHT = 1020;
    public static boolean V_SYNC = false;
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float FOV = (float) (120);
    public static final float MOUSE_SENSITIVITY = 0.03f;
    public static final Layout DEFAULT_LAYOUT = new VerticalLayout();
    public static boolean BLOOM_ON = true;
    public static boolean BLACK_AND_WHITE_ON = false;
    public static boolean COLOR_INVERT = false;
    public static boolean GAMMA_CORRECT = true;
    public static final Vector3f CAMERA_INIT_POS = new Vector3f(0.0f, 0.0f, 10.0f);
    public static final Vector3f CAMERA_INIT_ROT = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final Vector3f PLAYER_INIT_POSITION = new Vector3f(0.0f, 0.0f, 0.0f);

    public static final Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

    private SETTINGS(){}
}
