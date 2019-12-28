package sec.cyberprojectone.db;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final List<Property> properties = new ArrayList<>();
    public <T> void property(Getter<T> getter, Setter<T> setter) {
        properties.add(new Property(getter, setter));
    }

    Property getProperty(int index) {
        return properties.get(index);
    }

    @Override
    public String toString() {
        return "entity with " + properties.size() + " properties";
    }
}
