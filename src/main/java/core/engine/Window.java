package core.engine;

import core.inputs.JoystickListener;
import core.inputs.KeyListener;
import core.inputs.MouseListener;
import core.utils.AssetPool;
import core.utils.Time;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;

import static core.utils.SETTINGS.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long window;
    private static Scene sceneInstance = null;
    private static Window instance = null;

    private Window(){
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new RuntimeException("Failed to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        window = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, WIN_TITLE, NULL, NULL);

        if(window == NULL){
            throw new RuntimeException("Failed to create window.");
        }

        // [...legs]

        //callbacks and such...
        glfwSetKeyCallback(window, KeyListener::keyCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::scrollCallback);
        glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);

        //joystick call backs
        //JoystickListener joystickListener = JoystickListener.get();
        //glfwSetJoystickCallback(joystickListener);

        // Set up button callback
        //glfwSetJoystickButtonCallback(0, (joystick, button, action, mods) -> buttonCallback(button, action));
        //glfwSetJ

        glfwSetWindowSizeCallback(window, (w, width, height)->{
            setWidth(width);
            setHeight(height);
            glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
            //Camera.init();
        });

        GLFWImage image = GLFWImage.malloc();
        GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
        image.set(474, 474, AssetPool.loadImage("assets/images/icon.png"));
        imagebf.put(0, image);
        glfwSetWindowIcon(window, imagebf);

        image.free();
        imagebf.free();

        glfwMakeContextCurrent(window);

        if(V_SYNC){
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_MULTISAMPLE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        sceneInstance = new Scene();
    }

    public static Window get(){
        if(Window.instance == null){
            Window.instance = new Window();
        }
        return Window.instance;
    }

    public void run(){
        //System.out.println("JoyStick:  " + glfwGetJoystickName(GLFW_JOYSTICK_1));
        glfwSetCursorPos(Window.getWindow(), WIN_WIDTH/2f, WIN_HEIGHT/2f);

        float dt = 0;
        float startTime = Time.get();
        float endTime;

        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();

            if(dt > 0){
                glClearColor(0.23f, 0.18f, 0.33f,  1);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

                sceneInstance.updateInputs(dt);
                if(StateMachine.play()) sceneInstance.update(dt);
                sceneInstance.render(dt);
            }

            glfwSwapBuffers(window);

            endTime = Time.get();
            dt = endTime - startTime;
            startTime = endTime;
        }

        cleanup();
    }

    public static void cleanup(){
        //cleanup
        sceneInstance.dispose();
        glfwFreeCallbacks(window);
        glfwSetErrorCallback(null).free();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static long getWindow(){
        return window;
    }
    public void setWidth(int w){
        WIN_WIDTH = w;
    }
    public void setHeight(int h){
        WIN_HEIGHT = h;
    }

    public static int getWidth(){
        return WIN_WIDTH;
    }
    public static int getHeight(){
        return WIN_HEIGHT;
    }

}
