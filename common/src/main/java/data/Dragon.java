package data;


import utility.Validateable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Класс дракона
 *
 * @author steepikk
 */

public class Dragon implements Validateable, Comparable<Dragon>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long age; //Значение поля должно быть больше 0
    private String description; //Поле не может быть null
    private Color color; //Поле может быть null
    private DragonCharacter character; //Поле не может быть null
    private Person killer; //Поле может быть null

    private int creatorId;

    public Dragon(Integer id, String name, Coordinates coordinates, long age, String description, Color color, DragonCharacter character, Person killer) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.age = age;
        this.description = description;
        this.color = color;
        this.character = character;
        this.killer = killer;
        this.creatorId = 0;
    }

    public Dragon(Integer id, String name, Coordinates coordinates, long age, String description, Color color, DragonCharacter character, Person killer, int creatorId) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.age = age;
        this.description = description;
        this.color = color;
        this.character = character;
        this.killer = killer;
        this.creatorId = creatorId;
    }

    public Dragon(Integer id, String name, Coordinates coordinates, LocalDate creationDate, long age, String description, Color color, DragonCharacter character, Person killer, int creatorId) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.age = age;
        this.description = description;
        this.color = color;
        this.character = character;
        this.killer = killer;
        this.creatorId = creatorId;
    }

    @Override
    public boolean validate() {
        //if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (age <= 0) return false;
        if (color == null) return false;
        if (character == null) return false;
        return true;
    }

    /**
     * Обновляет дракон
     *
     * @param dragon
     */

    public void update(Dragon dragon) {
        this.name = dragon.name;
        this.coordinates = dragon.coordinates;
        this.creationDate = dragon.creationDate;
        this.age = dragon.age;
        this.description = dragon.description;
        this.color = dragon.color;
        this.character = dragon.character;
        this.killer = dragon.killer;
    }

    public Integer getId() {
        return id;
    }

    public long getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public Person getKiller() {
        return killer;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public Dragon copy(Integer id) {
        return new Dragon(id, this.name, this.coordinates, this.age, this.description, this.color, this.character, this.killer);
    }

    public Dragon copy(Integer id, int creatorId) {
        return new Dragon(id, this.name, this.coordinates, this.age, this.description, this.color, this.character, this.killer, creatorId);
    }

    @Override
    public int compareTo(Dragon dragon) {
        return (int) (this.id - dragon.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dragon dragon = (Dragon) o;
        return age == dragon.age && Objects.equals(id, dragon.id) && Objects.equals(name, dragon.name) && Objects.equals(coordinates, dragon.coordinates) && Objects.equals(creationDate, dragon.creationDate) && Objects.equals(description, dragon.description) && color == dragon.color && character == dragon.character && Objects.equals(killer, dragon.killer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, age, description, color, character, killer);
    }

    @Override
    public String toString() {
        return "id: " + (id == null ? "null" : "" + id.toString() + "") +
                "\nname: " + name +
                "\ncoordinates: " + coordinates +
                "\ncreationDate: " + creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                "\nage: " + age +
                "\ndescription: " + description +
                "\ncolor: " + color +
                "\ncharacter: " + (character == null ? "null" : character) +
                "\nkiller: " + (killer == null ? "null" : "" + killer) +
                "\ncreator_id: " + creatorId;
    }
}
