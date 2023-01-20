package orquest.test;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

public class TestUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    public static String readFile(String path) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + path).toPath(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static DataSource initDatabase(String schema) throws SQLException {
        return initDatabase(schema, null);
    }

    public static DataSource initDatabase(String schema, @Nullable String sqlScript) throws SQLException {
        JdbcConnectionPool connectionPool =
            JdbcConnectionPool.create("jdbc:h2:mem:" + schema + ";DB_CLOSE_DELAY=-1;", "sa", "");

        migrate(connectionPool, schema, "classpath:db/migration");

        if (sqlScript != null && !sqlScript.isEmpty()) {
            executeSql(connectionPool, sqlScript);
        }

        return connectionPool;
    }

    public static void executeSql(DataSource dataSource, String path) throws SQLException {
        LOGGER.info("Executing script=[{}] for datasource=[{}]", path, dataSource);

        Resource sqlFile = new ClassPathResource(path);

        Connection connection = dataSource.getConnection();

        try (connection) {
            connection.setAutoCommit(true);

            ScriptUtils.executeSqlScript(connection, sqlFile);
        }

        LOGGER.info("Script executed");
    }

    public static void migrate(DataSource dataSource, String...locations) {
        LOGGER.info("Running migrations");

        Flyway flyway =
            Flyway
                .configure()
                .dataSource(dataSource)
                .validateMigrationNaming(true)
                .locations(locations)
                .createSchemas(true)
                .load();
        flyway.migrate();

        LOGGER.info("Migrations completed");
    }
}
