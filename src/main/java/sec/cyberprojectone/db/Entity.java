package sec.cyberprojectone.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DbEntity
public abstract class Entity {
    final List<Property> properties = new ArrayList<>();
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

    protected final <T extends String> void property(
            Getter<T> getter,
            Setter<T> setter
    ) {
        int propIndex = properties.size();
        properties.add(new Property(
                columnName(propIndex), getter, setter, "varchar(255)"
        ));
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
