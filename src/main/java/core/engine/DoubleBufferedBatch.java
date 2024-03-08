package core.engine;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;
import static org.lwjgl.system.MemoryUtil.memFree;

public class DoubleBufferedBatch {
    //The sweet spot. More than this becomes slow and weird bugs develop (lines from the top left corner)
    protected final static int MAX_SIZE = 5000;
    //Using double buffering for the vbo
    private final ByteBuffer[] mappedBuffer;
    // Sync mapBuffer for synchronization
    //allows to synchronize between the CPU and GPU, ensuring that data is not accessed while being updated
    //private final long[] syncObjects;
    private final int[] vbo;
    private int activeVbo;
    private final int vao, ebo;
    private final int POS_SIZE = 3;
    private final int COL_SIZE = 4;
    private final int VERTEX_SIZE_BYTES = (POS_SIZE + COL_SIZE) * Float.BYTES;
    //current #of entities held -> to be drawn?
    private int numEntities;

    public DoubleBufferedBatch(){

        //init sync objects
        //this.syncObjects = new long[2];

        this.numEntities = 0;
        this.mappedBuffer = new ByteBuffer[2];
        this.activeVbo = 0;
        this.vbo = new int[2];

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        for(int i = 0; i < 2; i++) {

            long bufferSizeBytes = (long) MAX_SIZE * 4 * VERTEX_SIZE_BYTES;

            this.vbo[i] = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, this.vbo[i]);
            glBufferData(GL_ARRAY_BUFFER, bufferSizeBytes, GL_DYNAMIC_DRAW);

            // Map the buffer persistently
            //https://www.cppstories.com/2015/01/persistent-mapped-buffers-in-opengl/#synchronization
            this.mappedBuffer[i] = glMapBufferRange(GL_ARRAY_BUFFER, 0, MAX_SIZE * 4 * VERTEX_SIZE_BYTES, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT, null);

            // Unmap the buffer (it remains persistently mapped).
            glUnmapBuffer(GL_ARRAY_BUFFER);
        }

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

        //Choose activeBuffer
        ByteBuffer activeBuffer = this.mappedBuffer[activeVbo];

        float[] pVertices = this.getVertices(particle, false);
        long offset = (long) this.numEntities * 4 * VERTEX_SIZE_BYTES;

        // Update the buffer data directly through the mapped buffer
        activeBuffer.position((int) offset);
        for (float pVertex : pVertices)
            activeBuffer.putFloat(pVertex);

        // Flush the changes to the GPU
        glFlushMappedBufferRange(GL_ARRAY_BUFFER, offset, MAX_SIZE * 4 * VERTEX_SIZE_BYTES);

        //sync
        //this.sync();

        this.numEntities++;
        activeVbo ^= 1;
    }

    public void updateVertex(Particle particle, int index){

        //wait until the gpu is no longer using the buffer
        //waitBuffer();

        if(index < this.numEntities) {

            //Choose activeBuffer
            ByteBuffer activeBuffer = this.mappedBuffer[activeVbo];

            // Update the buffer data directly through the mapped buffer
            long offset = (long) index * 4 * VERTEX_SIZE_BYTES;
            float[] pVertices = this.getVertices(particle, false);

            activeBuffer.position((int) offset);
            for (float pVertex : pVertices)
                activeBuffer.putFloat(pVertex);

            // Flush the changes to the GPU
            glFlushMappedBufferRange(GL_ARRAY_BUFFER, offset, MAX_SIZE * 4 * VERTEX_SIZE_BYTES);

            //sync
            //this.sync();

            // Swap active VBO for the next frame
            activeVbo ^= 1;
        }
    }

    public void removeVertex(Particle particle, int index){
        if(index < this.numEntities) {

            //Choose activeBuffer
            ByteBuffer activeBuffer = this.mappedBuffer[activeVbo];

            // Update the buffer data directly through the mapped buffer
            long offset = (long) index * 4 * VERTEX_SIZE_BYTES;
            float[] pVertices = this.getVertices(particle, true);

            activeBuffer.position((int) offset);
            for (float pVertex : pVertices)
                activeBuffer.putFloat(pVertex);

            // Flush the changes to the GPU
            glFlushMappedBufferRange(GL_ARRAY_BUFFER, offset, MAX_SIZE * 4 * VERTEX_SIZE_BYTES);

            // Swap active VBO for the next frame
            activeVbo ^= 1;
        }
    }


//    private void sync() {
//        // Create a sync object
//        long syncObject = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
//
//        // Store the sync object in the array
//        this.syncObjects[activeVbo] = syncObject;
//
//        // Wait for the GPU to finish executing commands before proceeding
//        glWaitSync(syncObject, 0, GL_TIMEOUT_IGNORED);
//
//        // Cleanup
//        glDeleteSync(syncObject);
//    }

    private float[] getVertices(Particle particle, boolean remove){

        Vector4f color;
        Vector2f pos;
        float size;

        if(remove){
            color = new Vector4f(1.0f, 0.0f, 0.0f, 0.0f);
            pos = new Vector2f(Float.MIN_VALUE);
            size = 0;
        }
        else {
            color = particle.getColor();
            pos = particle.getPosition();
            size = particle.getSize();
        }

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

        for(int i = 0; i < 2; i++ ){
            glDeleteBuffers(this.vbo[i]);

            this.mappedBuffer[i].clear();
            glUnmapBuffer( GL_ARRAY_BUFFER );
            //memFree(this.mappedBuffer[i]);
        }
    }

    public boolean hasRoom(){
        return this.numEntities < MAX_SIZE;
    }

}
