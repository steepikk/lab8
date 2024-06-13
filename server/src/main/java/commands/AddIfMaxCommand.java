package commands;

import managers.CollectionManager;
import network.requests.AddIfMaxRequest;
import network.requests.Request;
import network.responses.AddIfMaxResponse;
import network.responses.Response;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его возраст превышает максимальный возраст этой коллекции.
 *
 * @author steepikk
 */
public class AddIfMaxCommand extends Command {
    private final CollectionManager collectionManager;

    public AddIfMaxCommand(CollectionManager collectionManager) {
        super("add_if_max {element}", "добавить новый элемент в коллекцию, если его возраст превышает максимальный возраст этой коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        try {
            var req = (AddIfMaxRequest) request;
            if (collectionManager.getCollection().size() == 0) {
                var newId = collectionManager.addToCollection(req.getUser(), req.dragon);
                return new AddIfMaxResponse(true, newId, null);
            } else if (collectionManager.greaterThanAll(req.dragon)) {
                var newId = collectionManager.addToCollection(req.getUser(), req.dragon);
                return new AddIfMaxResponse(true, newId, null);
            }
            return new AddIfMaxResponse(false, -1, null);
        } catch (Exception e) {
            return new AddIfMaxResponse(false, -1, e.toString());
        }
    }
}
