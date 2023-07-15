package core;

import core.inputs.KeyListener;
import core.inputs.MouseListener;
import core.scenes.Scene;
import core.utils.Constants;
import core.utils.Time;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static long window;
    private static Window instance = null;
    private Scene currentScene = null;
    private int WIN_WIDTH = 1600;
    private int WIN_HEIGHT = 900;

    private Window(){
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new RuntimeException("Failed to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        window = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, Constants.WIN_TITLE, NULL, NULL);

        if(window == NULL){
            throw new RuntimeException("Failed to create window.");
        }

        //keys callbacks and such...
        glfwSetKeyCallback(window, KeyListener::keyCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::scrollCallback);
        glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        glfwSetWindowSizeCallback(window, (w, width, height)->{
            setWidth(width);
            setHeight(height);
            glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        });

        glfwMakeContextCurrent(window);

        if(Constants.V_SYNC){
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        //glEnable(GL_FRAMEBUFFER_SRGB);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_MULTISAMPLE);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public static Window get(){
        if(Window.instance == null){
            Window.instance = new Window();
        }
        return Window.instance;
    }

    public void changeScene(Scene scene){
        this.currentScene = scene;
    }

    public void run(){

        float dt = 0;
        float startTime = Time.get();
        float endTime = 0;

        while(!glfwWindowShouldClose(window)){

            glfwPollEvents();

            //glClearColor(0.529f, 0.808f, 0.922f, 1.0f);
            glClearColor(0.0f, 0.01f, 0.04f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT |  GL_STENCIL_BUFFER_BIT);

            if(dt > 0){
                this.currentScene.update(dt);
            }

            glfwSwapBuffers(window);

            endTime = Time.get();
            dt = endTime - startTime;
            startTime = endTime;
        }

        //cleanup
        glfwFreeCallbacks(window);
        glfwSetErrorCallback(null).free();
        glfwDestroyWindow(window);
        glfwTerminate();
        this.currentScene.cleanup();
    }

    public static long getWindow(){
        return window;
    }
    public void setWidth(int w){
        get().WIN_WIDTH = w;
    }
    public void setHeight(int h){
        get().WIN_HEIGHT = h;
    }

    public static int getWidth(){
        return get().WIN_WIDTH;
    }
    public static int getHeight(){
        return get().WIN_HEIGHT;
    }

}
