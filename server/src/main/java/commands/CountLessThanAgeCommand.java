package commands;

import managers.CollectionManager;
import network.requests.CountLessThanAgeRequest;
import network.requests.Request;
import network.responses.CountLessThanAgeResponse;
import network.responses.Response;

/**
 * Команда 'count_less_than_age'. Выводит количества элементов, значение поля age которых меньше заданного.
 *
 * @author steepikk
 */
public class CountLessThanAgeCommand extends Command {
    private final CollectionManager collectionManager;

    public CountLessThanAgeCommand(CollectionManager collectionManager) {
        super("count_less_than_age <age>", "выводит количества элементов, значение поля age которых меньше заданного");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (CountLessThanAgeRequest) request;
        try {
            Integer countDragon = collectionManager.countLessThenAge(req.age);
            if (countDragon == 0) {
                return new CountLessThanAgeResponse(0, "Драконов, у которых поле age меньше заданной подстроки не обнаружено!");
            } else {
                return new CountLessThanAgeResponse(countDragon, null);
            }
        } catch (Exception e) {
            return new CountLessThanAgeResponse(0, e.toString());
        }
    }
}
