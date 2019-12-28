package sec.cyberprojectone.db;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.stereotype.Component;
import sec.cyberprojectone.Account;
import sec.cyberprojectone.LoginFailedException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class Database {
    public Database() throws Exception {
        // TODO: do something useful
        findEntities("sec.cyberprojectone")
                .forEach(System.out::println);
        createTables();
    }

    private List<DbEntity> findEntities(String basePackageName)
            throws Exception {
        ClassPathScanningCandidateComponentProvider p
                = new ClassPathScanningCandidateComponentProvider(false);
        p.addIncludeFilter(new AbstractClassTestingTypeFilter() {
            @Override
            protected boolean match(ClassMetadata metadata) {
                return Arrays.asList(metadata.getInterfaceNames())
                        .contains(DbEntity.class.getName());
            }
        });
        return p.findCandidateComponents(basePackageName).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(Database::createInstance)
                .collect(toList());
    }

    // TODO: replace this with something proper
    @SneakyThrows
    private static DbEntity createInstance(String className) {
        Class<?> c = Class.forName(className);
        return (DbEntity) c.newInstance();
    }

    private void createTables() throws SQLException {
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
