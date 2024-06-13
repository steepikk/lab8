package managers.db;

import dao.DragonDAO;
import dao.UserDAO;
import main.App;

import java.sql.Connection;
import java.sql.SQLException;

import static managers.db.ConnectionManager.getConnection;

/**
 * Класс для управления базой данных, создания базы данных и необходимых таблиц.
 *
 * @author steepikk
 */
public class DatabaseManager {
    private static final UserDAO userDAO = new UserDAO();
    private static final DragonDAO dragonDAO = new DragonDAO();

    /**
     * Создает базу данных, если она еще не существует, и инициализирует таблицы.
     *
     * @return Соединение с базой данных.
     */
    public static Connection createDatabaseIfNotExists() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                boolean databaseExists = checkDatabaseExists(connection);
                if (!databaseExists) {
                    App.logger.info("Database and tables created successfully.");
                } else {
                    App.logger.info("Database already exists.");
                }
                createTablesIfNotExist(connection);
            } else {
                App.logger.error("Failed to establish connection to the database.");
            }
            return connection;
        } catch (SQLException e) {
            App.logger.error("Error while creating database: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, существует ли база данных.
     *
     * @param connection Соединение с базой данных.
     * @return True, если база данных существует, false в противном случае.
     * @throws SQLException Если происходит ошибка SQL.
     */
    private static boolean checkDatabaseExists(Connection connection) throws SQLException {
        return connection.getMetaData().getCatalogs()
                .next(); // Check if the database exists by attempting to move to the first entry
    }

    /**
     * Создает необходимые таблицы, если они еще не существуют.
     *
     * @param connection Соединение с базой данных.
     */
    public static void createTablesIfNotExist(Connection connection) {
        if (connection != null) {
            userDAO.createTablesIfNotExist();
            dragonDAO.createTablesIfNotExist();
            App.logger.info("Tables created successfully (if not existed).");
        } else {
            App.logger.error("Connection is null.");
        }
    }
}
