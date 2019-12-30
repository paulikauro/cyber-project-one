package sec.cyberprojectone.db;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

public class StringSerializer {
    // TODO: only handles strings
    private static final String SEPARATOR = ".";
    public static <T extends Entity> String serialized(T ent) {
        return ent.properties.stream()
                .map(prop -> (String) prop.getGetter().get())
                .collect(joining(SEPARATOR));
    }

    public static <T extends Entity> void deserializedInto(T ent, String s) {
        // limit -1 in order to not remove empty fields
        String[] parts = s.split(Pattern.quote(SEPARATOR), -1);
        for (int i = 0; i < ent.properties.size(); i++) {
            ent.properties.get(i).getSetter().set(parts[i]);
        }
    }

    public static String serializedList(List<String> list) {
        return toHex(list.stream()
                .map(StringSerializer::toHex)
                .collect(joining(SEPARATOR)));
    }

    public static List<String> deserializedList(String s) {
        if (s.equals("")) {
            return new ArrayList<>();
        }
        s = fromHex(s);
        // limit -1 in order to not remove empty fields
        return Arrays
                .stream(s.split(Pattern.quote(SEPARATOR), -1))
                .map(StringSerializer::fromHex)
                .collect(toCollection(ArrayList::new));
    }

    private static String toHex(String s) {
        return DatatypeConverter.printHexBinary(s.getBytes());
    }

    private static String fromHex(String h) {
        return new String(DatatypeConverter.parseHexBinary(h));
    }
}
