package core.utils;

import core.ui.Layout;
import core.ui.VerticalLayout;

public final class SETTINGS {
    public final static String WIN_TITLE = "AEGIS Engine";
    public static int WIN_WIDTH = 1600;
    public static int WIN_HEIGHT = 900;
    public static boolean V_SYNC = false;
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final Layout DEFAULT_LAYOUT = new VerticalLayout();


    private SETTINGS(){}
}
