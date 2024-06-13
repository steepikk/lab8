package managers;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Класс для управления аутентификацией и регистрацией пользователей.
 *
 * @author steepikk
 */
public class AuthManager {
    private final Connection connection;
    private final int SALT_LENGTH = 10;

    private final Logger logger;

    /**
     * Конструктор AuthManager.
     *
     * @param connection Соединение с базой данных.
     * @param logger     Логгер для записи информации о действиях.
     */
    public AuthManager(Connection connection, Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return ID нового пользователя.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public int registerUser(String login, String password) throws SQLException {
        logger.info("Создание нового пользователя " + login);

        String salt = generateSalt();
        String passwordHash = generatePasswordHash(password, salt);

        String insertUserSQL = "INSERT INTO users (name, password_digest, salt) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, login);
            stmt.setString(2, passwordHash);
            stmt.setString(3, salt);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    logger.info("Пользователь успешно создан, id#" + userId);
                    return userId;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Аутентифицирует пользователя.
     *
     * @param login    Логин пользователя.
     * @param password Пароль пользователя.
     * @return ID аутентифицированного пользователя или 0, если аутентификация не удалась.
     * @throws SQLException Если происходит ошибка SQL.
     */
    public int authenticateUser(String login, String password) throws SQLException {
        logger.info("Аутентификация пользователя " + login);

        String selectUserSQL = "SELECT * FROM users WHERE name=?";
        try (PreparedStatement stmt = connection.prepareStatement(selectUserSQL)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Неправильный пароль для пользователя " + login);
                    return 0;
                }

                int id = rs.getInt("id");
                String salt = rs.getString("salt");
                String expectedHashedPassword = rs.getString("password_digest");

                String actualHashedPassword = generatePasswordHash(password, salt);
                if (expectedHashedPassword.equals(actualHashedPassword)) {
                    logger.info("Пользователь " + login + " аутентифицирован c id#" + id);
                    return id;
                }

                logger.warn("Неправильный пароль для пользователя " + login);
                return 0;
            }
        }
    }

    /**
     * Генерирует случайную строку для соли.
     *
     * @return Сгенерированная соль.
     */
    private String generateSalt() {
        return RandomStringUtils.randomAlphanumeric(SALT_LENGTH);
    }

    /**
     * Генерирует хэш пароля.
     *
     * @param password Пароль для хэширования.
     * @param salt     Соль для хэширования пароля.
     * @return Хэш пароля.
     */
    private String generatePasswordHash(String password, String salt) {
        String data = password + salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating password hash", e);
            return null;
        }
    }
}
