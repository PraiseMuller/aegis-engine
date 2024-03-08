package dev_test;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SandSimulation {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int NUM_PARTICLES = 10000;

    private long window;
    private int shaderProgram;
    private int vao;
    private int vbo;
    private float[] particles;
    private boolean pouring = false;

    private double lastMouseX, lastMouseY;

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Sand Simulation", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            lastMouseX = xpos / WIDTH * 2 - 1;
            lastMouseY = 1 - ypos / HEIGHT * 2;
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                pouring = action == GLFW_PRESS;
            }
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        initShaders();
        initParticles();
    }

    private void initShaders() {
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec2 position;\n" +
                "void main() {\n" +
                "   gl_Position = vec4(position, 0.0, 1.0);\n" +
                "}\0";

        String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main() {\n" +
                "   FragColor = vec4(0.8, 0.8, 0.6, 1.0);\n" +
                "}\0";

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void initParticles() {
        particles = new float[NUM_PARTICLES * 2];

        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles[i * 2] = (float) Math.random() * 2 - 1;
            particles[i * 2 + 1] = (float) Math.random() * 2 - 1;
        }

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, particles, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    private void update() {
        glfwPollEvents();

        // Apply gravity to all particles
        float gravity = -0.001f; // Adjust the gravity value
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles[i * 2 + 1] += gravity;
        }

        // Pour sand while the left mouse button is pressed
        if (pouring) {
            int particlesToAdd = 10;
            double radius = 0.02;

            for (int i = 0; i < particlesToAdd; i++) {
                int index = (i + particlesToAdd) % NUM_PARTICLES;

                double angle = Math.random() * 2 * Math.PI;
                double distance = Math.random() * radius;
                double x = lastMouseX + distance * Math.cos(angle);
                double y = lastMouseY + distance * Math.sin(angle);

                particles[index * 2] = (float) x;
                particles[index * 2 + 1] = (float) y;

                // Check for collisions with existing particles below
                for (int j = 0; j < NUM_PARTICLES; j++) {
                    if (i != j) {
                        double dx = particles[index * 2] - particles[j * 2];
                        double dy = particles[index * 2 + 1] - particles[j * 2 + 1];
                        double distanceSquared = dx * dx + dy * dy;

                        double minDistance = 0.02; // Adjust the minimum distance for interaction
                        if (distanceSquared < minDistance * minDistance) {
                            particles[index * 2 + 1] = (float) (particles[j * 2 + 1] + minDistance);
                        }
                    }
                }
            }

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, particles, GL_DYNAMIC_DRAW);
        }
    }


    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        glDrawArrays(GL_POINTS, 0, NUM_PARTICLES);

        glBindVertexArray(0);
        glUseProgram(0);

        glfwSwapBuffers(window);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            update();
            render();
        }
    }

    private void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        SandSimulation sandSimulation = new SandSimulation();
        sandSimulation.run();
    }

    private void run() {
        try {
            init();
            loop();
            cleanup();
        } finally {
            cleanup();
        }
    }
}
