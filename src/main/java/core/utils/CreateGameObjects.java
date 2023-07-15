package core.utils;

import core.models.GameObject;
import core.models.Material;
import core.models.Mesh;
import core.models.Texture;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Random;

public class CreateGameObjects {
    private static final Random r = new Random();
    private CreateGameObjects(){}

    public static List<GameObject> get(List<GameObject> objects, List<GameObject> lights){
        Material material;
        OBJ_Loader model3d = new OBJ_Loader("assets/3d-models/ground.obj");
        Mesh mesh = model3d.loadMesh();

        GameObject object;
        float scale = 25.0f;
        material = new Material(new Texture("assets/images/blu.png"), 50);
        material.setSpecularPower(128);

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 8; j++) {
                for(int k = 0; k < 6; k++) {
                    object = new GameObject(new Vector3f(i * 100, j * 100, k * 100), new Vector3f(1, r.nextFloat(0,1) * 10 * 3600,1), new Vector3f(scale, scale, scale), mesh, material);
                    objects.add(object);
                }
            }
        }

        //Light game object
        model3d = new OBJ_Loader("assets/3d-models/circle.obj");
        mesh = model3d.loadMesh();
        material = new Material(null, 0);
        material.setSpecularPower(0);

        object = new GameObject(new Vector3f(20, 400, 300), new Vector3f(), new Vector3f(20, 20, 20), mesh, material);
        object.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        lights.add(object);

        return objects;
    }
}


// = new GameObject(new Vector3f(50f, 0.0f, 10.1f), new Vector3f(), new Vector3f(6f,6f,6f), mesh, material);
//        object.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
//        objects.add(object);a

//        material = new Material(null, 9f);
//        material.setSpecularPower(32);
//        object = new GameObject(new Vector3f(13f, 0.0f, 10), new Vector3f(), new Vector3f(1f,1f,1f), mesh, material);
//        object.setColor(new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
//        objects.add(object);

//        material = new Material(null, 0.2f);
//        object = new GameObject(new Vector3f(-40, 0.0f, 10), new Vector3f(), new Vector3f(3f,3f,3f), mesh, material);
//        object.setColor(new Vector4f(0.0f, 1.1f, 0.0f, 1.0f));
//        objects.add(object);

//circle - 01
//        material = new Material(new Texture("assets/images/blu.png"), 0.5f);
//        mesh = model3d.loadMesh();
//        object = new GameObject(new Vector3f(-13f, 20.0f, 10.0f), new Vector3f(), new Vector3f(5f,5f,5f), mesh, material);
//        objects.add(object);

//circle - 02
//        material = new Material(null, 0.5f);
//        material.setSpecularPower(16);
//        object = new GameObject(new Vector3f(10.0f, 0.0f, 10.0f), new Vector3f(), new Vector3f(10f,10f,10f), mesh, material);
//        object.setColor(new Vector4f(0.7f, 0.1f, 0.4f, 1.0f));
//        objects.add(object);