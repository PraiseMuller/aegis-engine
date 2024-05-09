package core.entities;

public class VertexBoneData {

    public static final int  MAX_BONES_PER_VERTEX = 4;                //tweak based on model
    public int[] boneIds = new int[MAX_BONES_PER_VERTEX];
    public float[] weights = new float[MAX_BONES_PER_VERTEX];

    public VertexBoneData(){
        for (int i = 0; i < MAX_BONES_PER_VERTEX; i++){
            boneIds[i] = 0;
            weights[i] = 0.0f;
        }
    }

    public void addBoneData(int boneId, float weight){

        for (int i = 0; i < MAX_BONES_PER_VERTEX; i++){
            if(weights[i] == 0.0f){
                boneIds[i] = boneId;
                weights[i] = weight;
                //System.out.println("bone: "+boneId+"   weight: "+weight+"           index: "+i);
                return;
            }
        }

        throw new RuntimeException("More bones than we have space for.");   //should never get here
    }
}
