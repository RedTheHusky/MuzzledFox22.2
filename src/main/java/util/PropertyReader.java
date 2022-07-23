package util;

import java.util.ResourceBundle;

public class PropertyReader {
    private ResourceBundle properties;

    public PropertyReader(ResourceBundle properties) {
        this.properties = properties;
    }

    public String getValue(String key) throws Exception {
        checkKey(key);
        return properties.getString(key);
    }

    private void checkKey(String key) throws Exception {
        if(key == null || key.isBlank() || key.isEmpty()) throw new Exception("Passed invalid key");
    }
}
