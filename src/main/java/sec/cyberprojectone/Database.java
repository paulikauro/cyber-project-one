package sec.cyberprojectone;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class Database {
    public Database() throws SQLException {
        executeStatement(
                "CREATE TABLE IF NOT EXISTS users (username varchar(255), "
                        + "password varchar(255));"
        );
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
