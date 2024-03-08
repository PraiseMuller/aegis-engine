package core.ui;

public class VerticalLayout implements Layout {

    @Override
    public void layout(UIContainer container) {
        float yOffset = container.getHeight();  //start from the bottom
        for (UIElement child : container.getChildren()) {
            yOffset -= child.getHeight();
            child.setY(yOffset);
        }
    }
}
