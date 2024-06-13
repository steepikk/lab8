package network.requests;

import user.User;
import utility.Commands;

public class FilterLessThanCharacterRequest extends Request {
    public final String dragonCharacter;

    public FilterLessThanCharacterRequest(String dragonCharacter, User user) {
        super(Commands.FILTER_LESS_THAN_CHARACTER, user);
        this.dragonCharacter = dragonCharacter;
    }
}
