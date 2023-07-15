package core;

import core.scenes.Scene_001;

public class Launcher {
    public static void main(String args[]){
       Window windowInstance = Window.get();
       windowInstance.changeScene(new Scene_001());
       windowInstance.run();
    }
}
