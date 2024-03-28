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
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameOfLife {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private long window;
    private int shaderProgram;
    private int vao;
    private int vbo;
    private int ebo;
    private int numRows = 100;
    private int numCols = 100;
    private int[][] grid;
    private long lastTime;

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Game of Life", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
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
        initGrid();

        lastTime = System.currentTimeMillis();
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
                "   FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
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
        glBindAttribLocation(shaderProgram, 0, "position");
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void initGrid() {
        grid = new int[numRows][numCols];

        Random random = new Random();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = random.nextInt(2);
            }
        }

        float[] vertices = new float[]{
                -1.0f, 1.0f,   // Top-left
                1.0f, 1.0f,    // Top-right
                1.0f, -1.0f,   // Bottom-right
                -1.0f, -1.0f   // Bottom-left
        };

        int[] indices = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    private void updateGrid() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000) { // Update every second
            lastTime = currentTime;

            int[][] newGrid = new int[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    int neighbors = countNeighbors(i, j);

                    if (grid[i][j] == 1 && (neighbors < 2 || neighbors > 3)) {
                        // Any live cell with fewer than two live neighbors or more than three live neighbors dies
                        newGrid[i][j] = 0;
                    } else if (grid[i][j] == 0 && neighbors == 3) {
                        // Any dead cell with exactly three live neighbors becomes a live cell
                        newGrid[i][j] = 1;
                    } else {
                        // Any live cell with two or three live neighbors lives on to the next generation
                        newGrid[i][j] = grid[i][j];
                    }
                }
            }

            grid = newGrid;
        }
    }

    private int countNeighbors(int row, int col) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = (row + i + numRows) % numRows;
                int newCol = (col + j + numCols) % numCols;

                count += grid[newRow][newCol];
            }
        }

        count -= grid[row][col];

        return count;
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == 1) {
                    float x = (2.0f / numCols) * j - 1.0f;
                    float y = 1.0f - (2.0f / numRows) * i;

                    glLoadIdentity();
                    glTranslatef(x, y, 0.0f);

                    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                }
            }
        }

        glBindVertexArray(0);
        glUseProgram(0);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            updateGrid();
            render();
            glfwSwapBuffers(window);
        }
    }

    private void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteProgram(shaderProgram);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        GameOfLife gameOfLife = new GameOfLife();
        gameOfLife.run();
    }

    private void run() {
        try {
            init();
            loop();
        } finally {
            cleanup();
        }
    }
}
