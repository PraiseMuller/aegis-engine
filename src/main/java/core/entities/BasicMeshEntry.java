package core.entities;

public class BasicMeshEntry {

    protected int numIndices;
    protected int baseVertex;
    protected int baseIndex;
    protected int materialIndex;

    public BasicMeshEntry(){
        this.numIndices = 0;
        this.baseVertex = 0;
        this.baseIndex = 0;
        this.materialIndex = 0;
    }
}
