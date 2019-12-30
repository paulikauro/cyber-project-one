package sec.cyberprojectone.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sec.cyberprojectone.db.StringSerializer.deserializedList;
import static sec.cyberprojectone.db.StringSerializer.serializedList;

@DbEntity
public abstract class Entity {
    final List<Property> properties = new ArrayList<>();
    int primaryProp;
    // nice
    private static final String chars = "abcdefghijklmnopqrstuvwxyz";

    private static String columnName(int index) {
        return Character.toString(chars.charAt(index));
    }

    final Map<String, String> fields() {
        return properties.stream()
                .collect(Collectors.toMap(
                        Property::getColumnName,
                        Property::getDataType
                ));
    }

    private int addProperty(Getter g, Setter s, String dt) {
        int propIndex = properties.size();
        properties.add(new Property(columnName(propIndex), g, s, dt));
        return propIndex;
    }

    protected final <T extends String> int property(
            Getter<T> getter,
            Setter<T> setter
    ) {
        return addProperty(getter, setter, "varchar(255)");
    }

    protected final <T extends String> void primaryProperty(
            Getter<T> getter,
            Setter<T> setter
    ) {
        primaryProp = property(getter, setter);
    }

    protected final void list(
            Getter<List<String>> getter,
            Setter<List<String>> setter
    ) {
        Getter<String> serializedGetter =
                () -> serializedList(getter.get());
        Setter<String> deserializedSetter =
                s -> setter.set(deserializedList(s));
        addProperty(serializedGetter, deserializedSetter, "varchar(1023)");
    }

    public void pront() {
        System.out.println("entity " + tableName() + " with " + properties.size() +
                " properties: ");
        properties.forEach(
                prop -> System.out.println("\t" + prop)
        );
    }

    final String tableName() {
        return this.getClass().getSimpleName();
    }
}
