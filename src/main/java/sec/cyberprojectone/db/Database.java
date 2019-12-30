package sec.cyberprojectone.db;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public void delete(Entity ent) throws SQLException {
        Property primProp = ent.properties.get(ent.primaryProp);
        executeStatement(
                "DELETE FROM " + ent.tableName()
                + " WHERE " + primProp.getColumnName() + " = "
                        + quote((String) primProp.getGetter().get()) + ";"
        );
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
        try {
            delete(ent);
        } catch (SQLException e) {
            // ignored -- might exist
            // this is really hacky
        }
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

    @SneakyThrows
    public <T extends Entity> Stream<T> stream(Class<T> ent) {
        Entity e = ent.newInstance();
        Connection conn = null;
        ResultSet rs;
        try {
            conn = connect();
            rs = conn.createStatement().executeQuery("SELECT * FROM " + e.tableName() + ";");
        } catch (Exception ex) {
            if (conn != null) {
                conn.close();
            }
            throw ex;
        }
        DbStreamer streamer = new DbStreamer(conn, rs);
        return streamer.stream(streamConverter(ent));
    }

    private <T extends Entity> Function<ResultSet,T> streamConverter(Class<T> ent) {
        return new Function<ResultSet, T>() {
            @Override
            @SneakyThrows
            public T apply(ResultSet rs) {
                T t = ent.newInstance();
                Database.this.resultSetToEntity(rs, t);
                return t;
            }
        };
    }

    @SneakyThrows
    private void resultSetToEntity(ResultSet rs, Entity ent) {
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
