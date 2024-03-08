package core.ui;

import java.util.Map;

public class UIElement {
    private float x, y, width, height;
    private String tag;
    private Map<String, String> attributes;
    private Style style;

    public UIElement(String tag, float x, float y, float width, float height){
        this.tag = tag;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setStyle(Style style){
        this.style = style;
    }

    public Style getStyle(){
        return this.style;
    }

    public void render(){

    }


    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getTag() {
        return tag;
    }
}
