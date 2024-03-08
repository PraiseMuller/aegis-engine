package core.renderer;

import core.engine.Particle;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {
    private int vao, ebo, vbo;
    private FloatBuffer vertices;
    private int elementsCount;

    public Mesh(List<Particle> particles){
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        float[] v = this.getVertices(particles);
        this.vertices = MemoryUtil.memAllocFloat(v.length);
        this.vertices.put(v).flip();

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_DYNAMIC_DRAW);

        //EBO
        int[] indices = generateIndices(particles.size());
        this.elementsCount = indices.length;
        IntBuffer ib = MemoryUtil.memAllocInt(indices.length);
        ib.put(indices).flip();
        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 8 * Float.BYTES, 7 * Float.BYTES);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        memFree(ib);
    }

    public void update(int offset, List<Particle> particles) {
        // Update the VBO data for the specified cell
        float[] v = this.getVertices(particles);
        this.vertices.clear();
        this.vertices.put(v).flip();

        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, offset, this.vertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }


    private int[] generateIndices(int len){
        int[] elements = new int[6 * len];
        for(int i = 0; i < len; i++){
            int offSetArrayIndex = 6 * i;
            int offset = 4 * i;

            //triangle 1
            elements[offSetArrayIndex]     = offset + 3;
            elements[offSetArrayIndex + 1] = offset + 2;
            elements[offSetArrayIndex + 2] = offset + 0;

            //triangle 2
            elements[offSetArrayIndex + 3] = offset + 0;
            elements[offSetArrayIndex + 4] = offset + 2;
            elements[offSetArrayIndex + 5] = offset + 1;
        }
        return elements;
    }

    private float[] getVertices(List<Particle> particles){
        //Combine vertices
        List<Float> vertices = new ArrayList<>();

        float idx = 0;
        for(int i = 0; i < particles.size(); i++) {

            Vector4f color = particles.get(i).getColor();
            List<float[]> _vert = new ArrayList<>();

            Vector2f pos = particles.get(i).getPosition();
            float size = particles.get(i).getSize();

            //Position                                  Color                                  ID
            _vert.add(new float[]{pos.x,         pos.y,        -1.0f,     color.x, color.y, color.z, color.w,     idx});  //  1          2
            _vert.add(new float[]{pos.x + size,  pos.y,        -1.0f,     color.x, color.y, color.z, color.w,     idx});  //
            _vert.add(new float[]{pos.x + size,  pos.y + size, -1.0f,     color.x, color.y, color.z, color.w,     idx});  //
            _vert.add(new float[]{pos.x,         pos.y + size, -1.0f,     color.x, color.y, color.z, color.w,     idx});  //  4          3

            idx++;

            //add vert to vertices list.
            for(float[] vert : _vert){
                for (float f : vert){
                    vertices.add(f);
                }
            }
        }

        //VBO
        float[] v = new float[vertices.size()];
        for(int i = 0; i < v.length; i++)
            v[i] = vertices.get(i);

        return v;
    }

    public void draw(){
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, this.elementsCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void dispose(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);

        memFree(this.vertices);
    }
}
