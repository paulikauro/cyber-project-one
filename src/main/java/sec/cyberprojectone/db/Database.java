package sec.cyberprojectone.db;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.joining;
import static sec.cyberprojectone.db.EntityScanner.findEntities;

@Component
public class Database {
    private List<Entity> entities;

    public Database() throws Exception {
        // TODO: do something useful
        entities = findEntities("sec.cyberprojectone");
        entities.forEach(Entity::pront);
        createTables();
    }

    private void createTables() throws SQLException {
        for (Entity entity : entities) {
            createTable(entity);
        }
    }

    private void createTable(Entity ent)
            throws SQLException {
        String fieldSql = ent.fields().entrySet().stream()
                .map(e -> e.getKey() + " " + e.getValue())
                .collect(joining(", "));

        executeStatement(
                "CREATE TABLE IF NOT EXISTS "
                + ent.tableName()
                + " (" + fieldSql + ");"
        );
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
    }

    public void persist(Entity ent) throws SQLException {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        // TODO: update when persisting other stuff than strings
        ent.properties
                .forEach(prop -> {
                    names.add(prop.getColumnName());
                    values.add(quote((String) prop.getGetter().get()));
                });
        String nameSql = String.join(", ", names);
        String valueSql = String.join(", ", values);
        executeStatement(
                "INSERT INTO " + ent.tableName() + " (" + nameSql + ") VALUES "
                        + "(" + valueSql + ");"
        );
    }

    private String quote(String str) {
        return "'" + str + "'";
    }

    public <T> void loadInto(Entity ent, Getter<T> searchBy, Getter<T> actual)
            throws SQLException {
        AtomicBoolean found = new AtomicBoolean(false);
        executeQuery(
                "SELECT * FROM " + ent.tableName() + ";",
                rs -> {
                    while (rs.next()) {
                        resultSetToEntity(rs, ent);
                        if (actual.get().equals(searchBy.get())) {
                            found.set(true);
                            return;
                        }
                    }
                }
        );
        // TODO lol
        if (!found.get()) {
            throw new SQLException("not found");
        }
    }

    private void resultSetToEntity(ResultSet rs, Entity ent)
            throws SQLException {
        // TODO: only works with String
        for (Property prop : ent.properties) {
            prop.getSetter().set(rs.getString(prop.getColumnName()));
        }
    }

    private void executeStatement(String sql) throws SQLException {
        System.out.println("executing " + sql);
        try (Connection conn = connect()) {
            conn.createStatement().execute(sql);
        }
    }

    private void executeQuery(String sql, SQLConsumer<ResultSet> handler)
            throws SQLException {
        System.out.println("executing " + sql);
        try (Connection conn = connect()) {
            handler.accept(conn.createStatement().executeQuery(sql));
        }
    }

    @FunctionalInterface
    interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

}
