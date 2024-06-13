package managers;

import data.Dragon;
import data.DragonCharacter;
import exceptions.BadOwnerException;
import main.App;
import managers.db.PersistenceManager;
import user.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Класс для контроля коллекции
 *
 * @author steepikk
 */
public class CollectionManager {
    private HashSet<Dragon> collection = new HashSet<Dragon>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final PersistenceManager persistenceManager;
    private final ReentrantLock lock = new ReentrantLock();


    public CollectionManager(PersistenceManager persistenceManager) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.persistenceManager = persistenceManager;

        loadCollection();
        validateAll();
    }

    /**
     * Валидирует всех значений дракона
     */
    public void validateAll() {
        collection.stream()
                .filter(dragon -> !dragon.validate())
                .forEach(dragon -> App.logger.info("A dragon with id = " + dragon.getId()+ " has invalid fields."));
        App.logger.info("! Uploaded dragons are valid.");
    }

    /**
     * @return Коллекия
     */
    public HashSet<Dragon> getCollection() {
        return collection;
    }

    /**
     * @return Дата и время последней инициализации
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return Дата и время последнего сохранения
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return Тип коллекции
     */
    public String getType() {
        return collection.getClass().getName();
    }

    /**
     * @return Размер коллекции
     */
    public int getSize() {
        return collection.size();
    }

    /**
     * @return Последний элемент коллекции
     */
    public Dragon getLast() {
        if (collection.isEmpty()) return null;
        return collection.stream().reduce((one, two) -> two).get();
    }

    /**
     * Получает элемент коллекции по его айди
     *
     * @param id
     * @return элемент
     */
    public Dragon getById(int id) {
        return collection.stream()
                .filter(element -> element.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * @param id ID элемента.
     * @return Проверяет, существует ли элемент с таким ID.
     */
    public boolean checkExist(int id) {
        return getById(id) != null;
    }

    /**
     * @return Отсортированная коллекция.
     */
    public List<Dragon> sorted() {
        return new ArrayList<>(collection)
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, что данный элемент самый большой в коллекции
     *
     * @param dragon элемент
     */
    public boolean greaterThanAll(Dragon dragon) {
        return collection.stream().allMatch(element -> dragon.getAge() > element.getAge());
    }

    /**
     * Удаляет из коллекции и из базы данных все элементы больше заданного
     *
     * @param user Пользователь, выполняющий операцию
     * @param dragon Дракон, элементы больше которого нужно удалить
     * @throws SQLException Если возникла ошибка при взаимодействии с базой данных
     */
    public int removeGreater(User user, Dragon dragon, int id) throws SQLException, BadOwnerException {
        /*if (dragon.getCreatorId() != user.getId()) {
            App.logger.warn("Другой владелец. Исключение.");
            throw new BadOwnerException("Вы пытаетесь изменить данные другого пользователя!");
        }*/

        var removedCount = persistenceManager.removeGreater(user, dragon);
        if (removedCount == 0) {
            App.logger.warn("Ничего не было удалено.");
            return 0;
        }

        lock.lock();
        collection = collection.stream().filter(element -> dragon.compareTo(element) > 0 && element.getCreatorId() == user.getId()).collect(Collectors.toCollection(HashSet::new));
        lastSaveTime = LocalDateTime.now();
        lock.unlock();

        return removedCount;
    }

    /**
     * @return Коллекцию по возрастанию
     */
    public List<Dragon> printAscending() {
        Set<Dragon> tmpSet = new  HashSet<>(collection.stream().sorted().collect(Collectors.toList()));
        List<Dragon> tmpList = new ArrayList<Dragon>(tmpSet);
        return tmpList;
    }

    /**
     * @param character Характер дракона
     * @return Отфильтрованную коллекцию, где характер дракона меньше, чем указанный в подстроке
     */
    public List<Dragon> listLessThanCharacter(String character) {
        DragonCharacter dragonCharacter = DragonCharacter.valueOf(character);
        return collection.stream()
                .filter(element -> element.getCharacter().compareTo(dragonCharacter) < 0)
                .collect(Collectors.toList());
    }

    /**
     * @param age Характер дракона
     * @return Количество драконов возраст, которых меньше заданного в подстроке
     */
    public Integer countLessThenAge(String age) {
        Integer constantAge = Integer.parseInt(age);
        return (int) collection.stream()
                .filter(element -> element.getAge() < constantAge)
                .count();
    }


    /**
     * Добавляет элемент в коллекцию, обновляя id
     *
     * @param element Элемент для добавления
     */
    public int addToCollection(User user, Dragon element) throws SQLException {
        var newId = persistenceManager.add(user, element);
        App.logger.info("Новый дракон добавлен в БД.");

        lock.lock();
        collection.add(element.copy(newId, user.getId()));
        lastSaveTime = LocalDateTime.now();
        lock.unlock();

        App.logger.info("Дракон добавлен!");

        return newId;
    }

    /**
     * Обновляет элемент в коллекции.
     * @param user Пользователь.
     * @param element Элемент для обновления.
     */
    public void update(User user, Dragon element) throws SQLException, BadOwnerException {
        var dragon = getById(element.getId());
        if (dragon == null) {
            addToCollection(user, element);
        } else if (dragon.getCreatorId() == user.getId()) {
            App.logger.info("Обновление дракона id#" + dragon.getId() + " в БД.");

            persistenceManager.update(user, element);

            lock.lock();
            getById(element.getId()).update(element);
            lastSaveTime = LocalDateTime.now();
            lock.unlock();

            App.logger.info("Дракон успешно обновлен!ё");
        } else {
            App.logger.warn("Другой владелец. Исключение.");
            throw new BadOwnerException("Вы пытаетесь изменить данные другого пользователя!");
        }
    }

    /**
     * Удаляет элемент из коллекции.
     * @param id ID элемента для удаления.
     * @return количество удаленных драконов.
     */
    public int remove(User user, int id) throws SQLException, BadOwnerException {
        if (getById(id).getCreatorId() != user.getId()) {
            App.logger.warn("Другой владелец. Исключение.");
            throw new BadOwnerException("Вы пытаетесь изменить данные другого пользователя!");
        }

        var removedCount = persistenceManager.remove(user, id);
        if (removedCount == 0) {
            App.logger.warn("Ничего не было удалено.");
            return 0;
        }

        lock.lock();
        collection.removeIf(dragon -> dragon.getId() == id && dragon.getCreatorId() == user.getId());
        lastSaveTime = LocalDateTime.now();
        lock.unlock();

        return removedCount;
    }

    /**
     * Очищает коллекцию
     */
    public void clearCollection(User user) throws SQLException {
        persistenceManager.clear(user);

        lock.lock();
        collection.removeIf(dragon -> dragon.getCreatorId() == user.getId());
        lastSaveTime = LocalDateTime.now();
        lock.unlock();
    }


    /**
     * Загружает коллекцию из файла.
     */
    private void loadCollection() {
        try {
            collection = new HashSet<>(persistenceManager.loadDragons());
            lastInitTime = LocalDateTime.now();
        } catch (Exception e) {
            App.logger.error("Error loading collection from database", e);
        }
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";
        var last = getLast();

        StringBuilder info = new StringBuilder();
        for (Dragon dragon : collection) {
            info.append(dragon);
            if (dragon != last) info.append("\n\n");
        }
        return info.toString();
    }
}
