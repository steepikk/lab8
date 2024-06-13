package commands;

import managers.CollectionManager;
import network.requests.RemoveRequest;
import network.requests.Request;
import network.responses.RemoveResponse;
import network.responses.Response;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции.
 *
 * @author steepikk
 */
public class RemoveCommand extends Command {
    private final CollectionManager collectionManager;

    public RemoveCommand(CollectionManager collectionManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (RemoveRequest) request;

        try {
            if (!collectionManager.checkExist(req.id)) {
                return new RemoveResponse("Дракона с таким ID в коллекции нет!");
            }

            var removedCount = collectionManager.remove(req.getUser(), req.id);
            if (removedCount <= 0) {
                return new RemoveResponse("Ничего не удалено!");
            }
            return new RemoveResponse(null);
        } catch (Exception e) {
            return new RemoveResponse(e.toString());
        }
    }
}
