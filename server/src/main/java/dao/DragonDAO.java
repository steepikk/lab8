package dao;

import java.io.Serializable;
import java.sql.Connection;

import static managers.db.ConnectionManager.executeUpdate;
import static managers.db.ConnectionManager.getConnection;

/**
 * Класс для управления объектами типа Dragon в базе данных.
 *
 * @author steepikk
 */
public class DragonDAO implements Serializable {
    /**
     * SQL-запрос для создания таблицы драконов, если она не существует.
     */
    private static String CREATE_DRAGONS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS dragons (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR NOT NULL," +
            "x DOUBLE PRECISION NOT NULL," +
            "y INT NOT NULL," +
            "creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "age FLOAT NOT NULL," +
            "description VARCHAR NOT NULL," +
            "color VARCHAR(20) NOT NULL," +
            "character VARCHAR(20) NOT NULL," +
            "name_killer VARCHAR," +
            "weight FLOAT," +
            "eye_color VARCHAR(20)," +
            "hair_color VARCHAR(20)," +
            "creator_id INT NOT NULL," +
            "FOREIGN KEY (creator_id) REFERENCES users(id))";

    /**
     * Конструктор класса DragonDAO.
     * Создает новый объект {@code DragonDAO}.
     */
    public DragonDAO() {
    }

    /**
     * Создает таблицу драконов в базе данных, если она не существует.
     * Выполняет SQL-запрос {@link #CREATE_DRAGONS_TABLE_SQL} для создания таблицы.
     */
    public void createTablesIfNotExist() {
        Connection connection = getConnection();
        executeUpdate(connection, CREATE_DRAGONS_TABLE_SQL);
    }

}
