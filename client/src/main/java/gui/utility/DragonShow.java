package gui.utility;

import data.Dragon;
import data.Person;

public class DragonShow {
    private final Localizator localizator;

    public DragonShow(Localizator localizator) {
        this.localizator = localizator;
    }

    public String describe(Dragon dragon) {
        String info = "";

        info += "ID: " + dragon.getId();
        info += "\n " + localizator.getKeyString("Name") + ": " + dragon.getName();
        info += "\n " + localizator.getKeyString("OwnerID") + ": " + dragon.getCreatorId();
        info += "\n " + localizator.getKeyString("CreationDate") + ": " + localizator.getDate(dragon.getCreationDate());
        info += "\n X: " + dragon.getCoordinates().getX();
        info += "\n Y: " + dragon.getCoordinates().getY();
        info += "\n " + localizator.getKeyString("Age") + ": " + dragon.getAge();
        info += "\n " + localizator.getKeyString("Description") + ": " + dragon.getDescription();
        info += "\n " + localizator.getKeyString("Color") + ": " + dragon.getColor();
        info += "\n " + localizator.getKeyString("Character") + ": " + dragon.getCharacter();

        info += "\n" + localizator.getKeyString("Killer") + ": " + describePerson(dragon.getKiller());

        return info;
    }

    private String describePerson(Person killer) {
        if (killer == null) return ": null";

        String info = "";

        info += "\n    " + localizator.getKeyString("KillerName") + ": " + killer.getName();
        info += "\n    " + localizator.getKeyString("KillerWeight") + ": " + killer.getWeight();
        info += "\n    " + localizator.getKeyString("KillerEyeColor") + ": " + killer.getEyeColor();
        info += "\n    " + localizator.getKeyString("KillerHairColor") + ": " + killer.getHairColor();

        return info;
    }
}
