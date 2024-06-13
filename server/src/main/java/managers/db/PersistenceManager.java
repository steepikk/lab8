package managers.db;

import data.*;
import user.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для управления персистентностью данных о драконах и пользователях в базе данных.
 *
 * @author steepikk
 */
public class PersistenceManager {
    private final Connection connection;

    /**
     * Конструктор PersistenceManager.
     *
     * @param connection Соединение с базой данных.
     */
    public PersistenceManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * Добавляет дракона в базу данных.
     *
     * @param user   Пользователь, создавший дракона.
     * @param dragon Дракон для добавления.
     * @return ID добавленного дракона.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public int add(User user, Dragon dragon) throws SQLException {
        String insertDragonSQL = "INSERT INTO dragons (name, x, y, creation_date, age, description, color, character, name_killer, weight, eye_color, hair_color, creator_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertDragonSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dragon.getName());
            stmt.setDouble(2, dragon.getCoordinates().getX());
            stmt.setInt(3, dragon.getCoordinates().getY());
            stmt.setDate(4, Date.valueOf(dragon.getCreationDate()));
            stmt.setLong(5, dragon.getAge());
            stmt.setString(6, dragon.getDescription());
            stmt.setString(7, dragon.getColor().toString());
            stmt.setString(8, dragon.getCharacter().toString());

            Person killer = dragon.getKiller();
            if (killer != null) {
                stmt.setString(9, killer.getName());
                stmt.setLong(10, killer.getWeight());
                stmt.setString(11, killer.getEyeColor().toString());
                stmt.setString(12, killer.getHairColor().toString());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.BIGINT);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
            }

            stmt.setInt(13, user.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Inserting dragon failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting dragon failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Обновляет данные о драконе в базе данных.
     *
     * @param user   Пользователь, владеющий драконом.
     * @param dragon Обновленные данные о драконе.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public void update(User user, Dragon dragon) throws SQLException {
        String updateDragonSQL = "UPDATE dragons SET name=?, x=?, y=?, creation_date=?, age=?, description=?, color=?, character=?, name_killer=?, weight=?, eye_color=?, hair_color=? WHERE id=? AND creator_id=?";

        try (PreparedStatement stmt = connection.prepareStatement(updateDragonSQL)) {
            stmt.setString(1, dragon.getName());
            stmt.setDouble(2, dragon.getCoordinates().getX());
            stmt.setInt(3, dragon.getCoordinates().getY());
            stmt.setDate(4, Date.valueOf(dragon.getCreationDate()));
            stmt.setLong(5, dragon.getAge());
            stmt.setString(6, dragon.getDescription());
            stmt.setString(7, dragon.getColor().toString());
            stmt.setString(8, dragon.getCharacter().toString());

            Person killer = dragon.getKiller();
            if (killer != null) {
                stmt.setString(9, killer.getName());
                stmt.setLong(10, killer.getWeight());
                stmt.setString(11, killer.getEyeColor().toString());
                stmt.setString(12, killer.getHairColor().toString());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.BIGINT);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
            }

            stmt.setInt(13, dragon.getId());
            stmt.setInt(14, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Updating dragon failed, no rows affected.");
            }
        }
    }

    /**
     * Удаляет всех драконов, созданных пользователем.
     *
     * @param user Пользователь, создавший драконов.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public void clear(User user) throws SQLException {
        String deleteDragonsSQL = "DELETE FROM dragons WHERE creator_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteDragonsSQL)) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Удаляет дракона по его ID, если его создал данный пользователь.
     *
     * @param user Пользователь, создавший дракона.
     * @param id   ID дракона для удаления.
     * @return Количество удаленных строк.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public int remove(User user, int id) throws SQLException {
        String deleteDragonSQL = "DELETE FROM dragons WHERE id=? AND creator_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteDragonSQL)) {
            stmt.setInt(1, id);
            stmt.setInt(2, user.getId());
            return stmt.executeUpdate();
        }
    }

    /**
     * Удаляет всех драконов, созданных пользователем, возраст и характер которых больше заданных значений.
     *
     * @param user   Пользователь, создавший драконов.
     * @param dragon Дракон с пороговыми значениями для удаления.
     * @return Количество удаленных строк.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public int removeGreater(User user, Dragon dragon) throws SQLException {
        String deleteDragonSQL = "DELETE FROM dragons WHERE age > ? AND character > ? AND creator_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteDragonSQL)) {
            stmt.setLong(1, dragon.getAge());
            stmt.setString(2, dragon.getCharacter().toString());
            stmt.setInt(3, user.getId());
            return stmt.executeUpdate();
        }
    }

    /**
     * Загружает всех драконов из базы данных.
     *
     * @return Список драконов.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public List<Dragon> loadDragons() throws SQLException {
        List<Dragon> dragons = new ArrayList<>();
        String selectDragonsSQL = "SELECT * FROM dragons";
        try (PreparedStatement stmt = connection.prepareStatement(selectDragonsSQL)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    Coordinates coordinates = new Coordinates(rs.getDouble("x"), rs.getInt("y"));
                    LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
                    long age = rs.getLong("age");
                    String description = rs.getString("description");
                    Color color = Color.valueOf(rs.getString("color"));
                    DragonCharacter character = DragonCharacter.valueOf(rs.getString("character"));
                    Person killer = null;
                    if (rs.getString("name_killer") != null) {
                        killer = new Person(rs.getString("name_killer"), rs.getLong("weight"), Color.valueOf(rs.getString("eye_color")), Color.valueOf(rs.getString("hair_color")));
                    }
                    int creator_id = rs.getInt("creator_id");
                    //Dragon dragon = new Dragon(rs.getInt("id"), rs.getString("name"), new Coordinates(rs.getDouble("x"), rs.getInt("y")), rs.getDate("creation_date").toLocalDate(), rs.getLong("age"), rs.getString("description"), Color.valueOf(rs.getString("color")), DragonCharacter.valueOf(rs.getString("character")), new Person(rs.getString("name_killer"), rs.getLong("weight"), Color.valueOf(rs.getString("eye_color")), Color.valueOf(rs.getString("hair_color"))), rs.getInt("creator_id"));
                    Dragon dragon = new Dragon(id, name, coordinates, creationDate, age, description, color, character, killer, creator_id);
                    dragons.add(dragon);
                }
            }
        }
        return dragons;
    }
}
