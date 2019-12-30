package sec.cyberprojectone.db;

import java.util.List;

class SQL {
    static String insert(
            String table,
            List<String> names,
            List<String> values
    ) {
        String nameSql = String.join(", ", names);
        String valueSql = String.join(", ", values);
        return "INSERT INTO " + table + " (" + nameSql + ") VALUES "
                + "(" + valueSql + ");";
    }
}
