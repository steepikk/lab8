package user;

import java.io.Serializable;

/**
 * Класс, представляющий пользователя системы.
 *
 * @author steepikk
 */
public class User implements Comparable<User>, Serializable {
    private final int id;
    private final String name;
    private final String password;

    /**
     * Конструктор для создания объекта пользователя.
     *
     * @param id       ID пользователя.
     * @param name     Имя пользователя.
     * @param password Пароль пользователя.
     */
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Проверяет, допустима ли информация о пользователе.
     *
     * @return true, если длина имени пользователя меньше 40 символов, иначе false.
     */
    public boolean validate() {
        return getName().length() < 40;
    }

    /**
     * Создает копию пользователя с новым ID.
     *
     * @param id Новый ID для копии пользователя.
     * @return Новый объект пользователя с указанным ID.
     */
    public User copy(int id) {
        return new User(id, getName(), getPassword());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int compareTo(User user) {
        return (int) (this.id - user.getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='********'" +
                '}';
    }
}
