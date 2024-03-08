package core.ui;

import java.util.HashMap;
import java.util.Map;

public class Style {
    private Map<String, String> properties;

    public Style() {
        this.properties = new HashMap<>();
    }

    //Have premade properties here later?
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }
}
