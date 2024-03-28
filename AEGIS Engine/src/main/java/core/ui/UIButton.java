package core.ui;

public class UIButton extends UIElement{

    public UIButton(String tag, float x, float y, float width, float height) {
        super(tag, x, y, width, height);
    }

    // TODO : Add butt callbacks

    @Override
    public void render(){
        super.render();

        // TODO : render butt specific stuff here later (Textured quads ?)
    }
}
