package core.ui;

import java.util.ArrayList;
import java.util.List;

public class UIManager {
    private final List<UIContainer> containers;

    public UIManager() {
        this.containers = new ArrayList<>();
    }

    public void addContainer(UIContainer container) {
        containers.add(container);
    }

    public void render() {
        for (UIContainer container : this.containers) {
            container.render();
        }
    }

    // Add input handling, layout management, etc.
}
