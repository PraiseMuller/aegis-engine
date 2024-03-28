package core.engine;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class DoubleBufferedBatch {
    //The sweet spot. More than this becomes slow and weird bugs develop (lines from the top left corner)
    protected final static int MAX_SIZE = 5000;
    private final int vao, ebo, vbo;
    private final int POS_SIZE = 3;
    private final int COL_SIZE = 4;
    private final int VERTEX_SIZE_BYTES = (POS_SIZE + COL_SIZE) * Float.BYTES;
    //current #of entities held -> to be drawn?
    private int numEntities;

    public DoubleBufferedBatch(){

        this.numEntities = 0;

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        long bufferSizeBytes = (long) MAX_SIZE * 4 * VERTEX_SIZE_BYTES;

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);

        //EBO
        int[] indices = generateIndices(MAX_SIZE * 6);
        IntBuffer ib = MemoryUtil.memAllocInt(indices.length);
        ib.put(indices).flip();

        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, 0);
        glVertexAttribPointer(1, COL_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_SIZE * Float.BYTES);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        memFree(ib);
    }

    public void addVertex(Particle particle){

        long offset = (long) this.numEntities * 4 * VERTEX_SIZE_BYTES;
        float[] pVertices = this.getVertices(particle);

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, offset, pVertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        this.numEntities++;
    }

    public void updateVertex(Particle particle, int index){
        if(index < this.numEntities) {
            // Update the buffer data directly through the mapped buffer
            long offset = (long) index * 4 * VERTEX_SIZE_BYTES;
            float[] pVertices = this.getVertices(particle);

            glBindVertexArray(this.vao);
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferSubData(GL_ARRAY_BUFFER, offset, pVertices);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void removeVertex(Particle particle, int index){
        if(index <= this.numEntities) {

            long offset = (long) index * 4 * VERTEX_SIZE_BYTES;
            float[] pVertices = this.getVertices(particle);

            glBindVertexArray(this.vao);
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
            glBufferSubData(GL_ARRAY_BUFFER, offset, new float[pVertices.length]);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            this.numEntities--;
        }
    }

    private float[] getVertices(Particle particle){

        Vector4f color = particle.getColor();
        Vector2f pos = particle.getPosition();
        float size = particle.getSize();

        List<float[]> _vert = new ArrayList<>();
        _vert.add(new float[]{pos.x,         pos.y,        -1.0f,     color.x, color.y, color.z, color.w});  //  1          2
        _vert.add(new float[]{pos.x + size,  pos.y,        -1.0f,     color.x, color.y, color.z, color.w});  //
        _vert.add(new float[]{pos.x + size,  pos.y + size, -1.0f,     color.x, color.y, color.z, color.w});  //
        _vert.add(new float[]{pos.x,         pos.y + size, -1.0f,     color.x, color.y, color.z, color.w});  //  4          3

        //add vert to vertices list.
        float[] _vertices = new float[_vert.size() * 7];
        int idx = 0;
        for(float[] vert : _vert){
            for (float f : vert){
                _vertices[idx++] = f;
            }
        }

        return _vertices;
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

    public void render(){
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numEntities * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void dispose(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.ebo);
        glDeleteBuffers(this.vbo);
    }

    public boolean hasRoom(){
        return this.numEntities < MAX_SIZE;
    }

}
