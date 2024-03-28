package core.ui;

public class HorizontalLayout implements Layout{

    @Override
    public void layout(UIContainer container) {
        float xOffset = 0;
        for (UIElement child : container.getChildren()) {
            child.setX(xOffset);
            xOffset += child.getWidth();
        }
    }
}
