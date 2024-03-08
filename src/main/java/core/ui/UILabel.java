package core.ui;

public class UILabel extends UIElement {
    private String text;

    public UILabel(float x, float y, float width, float height, String text) {
        super("tag", x, y, width, height);
        this.text = text;
    }

    @Override
    public void render() {
        super.render();
        // TODO : specific logic, e.g., draw text
    }
}
