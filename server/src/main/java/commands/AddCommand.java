package commands;

import managers.CollectionManager;
import network.requests.AddRequest;
import network.requests.Request;
import network.responses.AddResponse;
import network.responses.Response;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author steepikk
 */
public class AddCommand extends Command {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (AddRequest) request;
        try {
            if (!req.dragon.validate()) {
                return new AddResponse(-1, "Поля дракона не валидны! Дракон не добавлен!");
            }
            var newId = collectionManager.addToCollection(req.getUser(), req.dragon);
            return new AddResponse(newId, null);
        } catch (Exception e) {
            return new AddResponse(-1, e.toString());
        }
    }
}
