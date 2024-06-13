package dao;

import java.sql.Connection;

import static managers.db.ConnectionManager.executeUpdate;
import static managers.db.ConnectionManager.getConnection;

/**
 * Класс для управления объектами типа User в базе данных.
 *
 * @author steepikk
 */
public class UserDAO {
    /**
     * SQL-запрос для создания таблицы пользователей, если она не существует.
     */
    private static final String CREATE_USERS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR(50) UNIQUE NOT NULL," +
            "password_digest VARCHAR(256) NOT NULL," +
            "salt VARCHAR(32) NOT NULL," +
            "registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "last_login TIMESTAMP)";

    /**
     * Конструктор класса UserDAO.
     * Создает новый объект {@code UserDAO}.
     */
    public UserDAO() {
    }

    /**
     * Создает таблицу пользователей в базе данных, если она не существует.
     * Выполняет SQL-запрос {@link #CREATE_USERS_TABLE_SQL} для создания таблицы.
     */
    public void createTablesIfNotExist() {
        Connection connection = getConnection();
        executeUpdate(connection, CREATE_USERS_TABLE_SQL);
    }
}

