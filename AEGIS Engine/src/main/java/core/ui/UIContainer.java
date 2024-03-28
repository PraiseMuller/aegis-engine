package core.ui;

import core.utils.SETTINGS;

import java.util.ArrayList;
import java.util.List;

public class UIContainer {
    private float x, y, width, height;
    private List<UIElement> children;
    private Layout layout;

    public UIContainer(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children = new ArrayList<>();
        this.layout = SETTINGS.DEFAULT_LAYOUT;
    }

    public void addElement(UIElement child){
        this.children.add(child);
    }

    public void removeElement(UIElement child){
        if(this.children.contains(child)){
            this.children.remove(child);
        }
        else
            throw new RuntimeException("Tried to remove UIElement that does not exist.");
    }

    public void render(){
        this.layout.layout(this);  //apply layout
        for (UIElement element : this.children){
            element.render();
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public List<UIElement> getChildren() {
        return children;
    }

    public void setChildren(List<UIElement> children) {
        this.children = children;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
