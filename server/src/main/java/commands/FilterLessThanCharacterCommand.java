package commands;

import managers.CollectionManager;
import network.requests.FilterLessThanCharacterRequest;
import network.requests.Request;
import network.responses.FilterLessThanCharacterResponse;
import network.responses.Response;

/**
 * Команда 'filter_less_than_character'. Выводит элементы, значение поля character которых меньше заданного.
 *
 * @author steepikk
 */
public class FilterLessThanCharacterCommand extends Command {
    private final CollectionManager collectionManager;

    public FilterLessThanCharacterCommand(CollectionManager collectionManager) {
        super("filter_less_than_character <character>", "выводит элементы, значение поля character которых меньше заданного");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (FilterLessThanCharacterRequest) request;
        try {
            var list = collectionManager.listLessThanCharacter(req.dragonCharacter);
            if (list.isEmpty()) {
                return new FilterLessThanCharacterResponse(null, "Драконов, у которых поле character меньше заданной подстроки не обнаружено!");
            } else {
                return new FilterLessThanCharacterResponse(list, null);
            }
        } catch (Exception e) {
            return new FilterLessThanCharacterResponse(null, e.toString());
        }
    }
}
