package dev_test;

import core.renderer.Window;

public class Launcher {
    public static void main(String[] args){
       Window windowInstance = Window.get();
       windowInstance.run();
    }
}
