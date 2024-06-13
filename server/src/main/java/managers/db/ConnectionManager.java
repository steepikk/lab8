package managers.db;

import main.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс для управления подключениями к базе данных и выполнения SQL-запросов.
 *
 * @author steepikk
 */
public class ConnectionManager {
    /*public static final String DB_URL = "jdbc:postgresql://pg:5432/";
    public static final String DB_NAME = "studs";*/
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "lab_7";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Stepan2005";

    /**
     * Получает соединение с базой данных.
     *
     * @return Соединение с базой данных.
     */
    public static Connection getConnection() {
        try {
            //return DriverManager.getConnection(DB_URL + DB_NAME);
            return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            App.logger.error("Connection failed", e);
            return null;
        }
    }

    /**
     * Закрывает соединение с базой данных.
     *
     * @param connection Соединение с базой данных для закрытия.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                App.logger.error("Error closing connection", e);
            }
        }
    }

    /**
     * Создает объект Statement для выполнения SQL-запросов.
     *
     * @param connection Соединение с базой данных.
     * @return Объект Statement для выполнения SQL-запросов или null, если не удалось создать Statement.
     */
    private static Statement createStatement(Connection connection) {
        if (connection == null) {
            return null;
        }
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            App.logger.error("Error creating statement", e);
            return null;
        }
    }

    /**
     * Выполняет SQL-запрос на обновление, используя заданный объект Statement.
     *
     * @param statement Объект Statement для выполнения запроса.
     * @param sql       SQL-запрос на обновление.
     */
    public static void executeUpdate(Statement statement, String sql) {
        if (statement == null) {
            return;
        }
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            App.logger.error("Error executing update", e);
        }
    }

    /**
     * Выполняет SQL-запрос на обновление, используя заданное соединение.
     *
     * @param connection Соединение с базой данных.
     * @param sql        SQL-запрос на обновление.
     */
    public static void executeUpdate(Connection connection, String sql) {
        Statement statement = createStatement(connection);
        executeUpdate(statement, sql);
    }
}
