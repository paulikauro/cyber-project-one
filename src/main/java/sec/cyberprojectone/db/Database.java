package sec.cyberprojectone.db;

import org.springframework.stereotype.Component;
import sec.cyberprojectone.Account;
import sec.cyberprojectone.LoginFailedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Component
public class Database {
    private EntityScanner scanner;

    public Database() throws Exception {
        // TODO: do something useful
        scanner = new EntityScanner("sec.cyberprojectone");
        scanner.printEntities();
        createTables();
    }

    private void createTables() throws SQLException {
        createTable(
                "users",
                new HashMap<String, String>() {{
                    put("username", "varchar(255)");
                    put("password", "varchar(255)");
                }});
    }

    private void createTable(String tableName, Map<String, String> fields)
            throws SQLException {
        String fieldSql = fields.entrySet().stream()
                .map(e -> e.getKey() + " " + e.getValue())
                .collect(joining(", "));
        String sql = "CREATE TABLE IF NOT EXISTS "
                + tableName
                + " (" + fieldSql + ");";
        executeStatement(sql);
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
    }

    public void persist(Account acc) throws SQLException {
        executeStatement(
                "INSERT INTO users (username, password) VALUES "
                        + "('" + acc.username + "', '" + acc.password + "');"
        );
    }

    public void loadAccount(Account acc)
            throws SQLException, LoginFailedException {
        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT password FROM users WHERE username = '" + acc.username + "';"
            );
            rs.next();
            String realPass = rs.getString("password");
            // TODO: business logic, move elsewhere
            if (!realPass.equals(acc.password)) {
                throw new LoginFailedException();
            }
        }
    }

    private void executeStatement(String sql) throws SQLException {
        System.out.println("executing " + sql);
        try (Connection conn = connect()) {
            conn.createStatement().execute(sql);
        }
    }

}
