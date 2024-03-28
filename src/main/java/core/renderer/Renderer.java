package core.renderer;

import core.engine.*;
import core.postprocess.FrameBuffer;
import core.postprocess.Quad;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final ImGuiLayer imGuiLayer;
    //private final BatchManager batchManager;
    private final Quad bwTestQuad;

    public Renderer(List<Particle> particles){
        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();

//        this.batchManager = new BatchManager();
//        for(Particle particle : particles){
//            this.addVertex(particle);
//        }
//
        this.bwTestQuad = new Quad();
    }

    public void addVertex(Particle particle){
        //this.batchManager.addVertex(particle);
    }

    public void updateVertex(Particle particle, int index){
        //this.batchManager.updateVertex(particle, index);
    }

    public void removeVertex(Particle particle, int index){
        //this.batchManager.removeVertex(particle, index);
    }

    public void render(Scene scene, float dt){
        //  1-ST PASS
        //this.bwFramebuffer.bind();
        glClearColor(0.23f, 0.18f, 0.33f,  1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        //this.batchManager.render();
        scene.getPlayer().render();
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        //  2-ND PASS
        //this.bwFramebuffer.unbind();
        //glClearColor(0.23f, 0.18f, 0.33f,  1);
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        imGuiLayer.update(dt, scene);
        this.bwTestQuad.render();
        //////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void dispose(){
        this.imGuiLayer.destroyImGui();
        //this.batchManager.dispose();
        this.bwTestQuad.dispose();
    }
}
