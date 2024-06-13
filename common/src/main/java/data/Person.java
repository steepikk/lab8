package data;

import utility.Validateable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Класс человека
 *
 * @author steepikk
 */

public class Person implements Validateable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long weight; //Значение поля должно быть больше 0
    private Color eyeColor; //Поле не может быть null
    private Color hairColor; //Поле может быть null

    public Person(String name, long weight, Color eyeColor, Color hairColor) {
        this.name = name;
        this.weight = weight;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
    }

    public String getName() {
        return name;
    }

    public long getWeight() {
        return weight;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (weight <= 0) return false;
        if (eyeColor == null) return false;
        if (hairColor == null) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return weight == person.weight && Objects.equals(name, person.name) && eyeColor == person.eyeColor && hairColor == person.hairColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, eyeColor, hairColor);
    }

    @Override
    public String toString() {
        return name +
                "\nweight_killer: " + weight +
                "\neye_color_killer: " + (eyeColor == null ? "null" : eyeColor) +
                "\nhair_color_killer: " + (hairColor == null ? "null" : hairColor);
    }
}
