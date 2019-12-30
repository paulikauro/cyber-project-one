package sec.cyberprojectone.db;

public class StringSerializer {
    // TODO: only handles strings
    private static final String SEPARATOR = ".";
    public static <T extends Entity> String serialized(T ent) {
        StringBuilder sb = new StringBuilder();
        ent.properties.forEach(prop -> {
            sb.append(prop.getGetter().get());
            sb.append(SEPARATOR);
        });
        return sb.toString();
    }

    public static <T extends Entity> void deserializedInto(T ent, String s) {
        String[] parts = s.split(SEPARATOR);
        for (int i = 0; i < ent.properties.size(); i++) {
            ent.properties.get(i).getSetter().set(parts[i]);
        }
    }
}
