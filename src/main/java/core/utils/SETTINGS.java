package core.utils;

import core.ui.Layout;
import core.ui.VerticalLayout;
import org.joml.Vector4f;

public final class SETTINGS {
    public final static String WIN_TITLE = "AEGIS Engine";
    public static int WIN_WIDTH = 1600;
    public static int WIN_HEIGHT = 900;
    public static boolean V_SYNC = false;
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final Layout DEFAULT_LAYOUT = new VerticalLayout();
    public static boolean BLOOM_ON = true;

    //COLORS
    public static final Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Vector4f SCOL = new Vector4f(0.23f, 0.18f, 0.33f,  1.0f);


    private SETTINGS(){}
}
